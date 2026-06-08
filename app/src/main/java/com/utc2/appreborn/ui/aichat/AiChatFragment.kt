package com.utc2.appreborn.ui.aichat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Context
import org.json.JSONObject
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import kotlinx.coroutines.launch
import retrofit2.awaitResponse
import com.utc2.appreborn.network.ApiClient
import com.utc2.appreborn.network.AiChatApiService
import com.utc2.appreborn.network.dto.AiChatRequest
import com.utc2.appreborn.network.dto.AiChatMessageDto
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import com.utc2.appreborn.R

class AiChatFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    AiChatScreen(
                        onBackClick = {
                            requireActivity().supportFragmentManager.popBackStack()
                        },
                        onNavigate = { route ->
                            val activity = requireActivity()
                            if (activity is com.utc2.appreborn.ui.main.MainActivity) {
                                activity.navigateFromChat(route)
                            }
                        }
                    )
                }
            }
        }
    }
}

data class ChatSuggestion(val text: String, val actionPath: String? = null)

data class ActionConfig(val text: String, val route: String)

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val actionId: String? = null,
    val documentTitle: String? = null,
    val documentSource: String? = null,
    val confidenceScore: Double? = null,
    val actionButtons: List<com.utc2.appreborn.network.dto.ActionButtonDto>? = null
)

fun loadChatActions(context: Context): Map<String, ActionConfig> {
    val map = mutableMapOf<String, ActionConfig>()
    try {
        val jsonString = context.assets.open("chat_actions.json").bufferedReader().use { it.readText() }
        val jsonObject = JSONObject(jsonString)
        jsonObject.keys().forEach { key ->
            val obj = jsonObject.getJSONObject(key)
            map[key] = ActionConfig(
                text = obj.getString("actionText"),
                route = obj.getString("route")
            )
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return map
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiChatScreen(onBackClick: () -> Unit, onNavigate: (String) -> Unit) {
    var inputText by remember { mutableStateOf(TextFieldValue("")) }
    var messages by remember {
        mutableStateOf(
            listOf(
                ChatMessage("Xin chào! Tôi có thể giúp gì cho bạn hôm nay?", isUser = false)
            )
        )
    }

    var suggestions by remember {
        mutableStateOf(
            listOf(
                ChatSuggestion("Xem lịch học ở đâu?"),
                ChatSuggestion("Xem điểm thi"),
                ChatSuggestion("Hướng dẫn đăng ký tín chỉ")
            )
        )
    }

    val context = LocalContext.current
    val chatActions = remember { loadChatActions(context) }
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }

    fun handleSend(text: String, action: String? = null, actionId: String? = null) {
        if (text.isBlank() && action == null) return
        
        if (text.isNotBlank()) {
            messages = messages + ChatMessage(text, true)
            inputText = TextFieldValue("")
        }

        val request = AiChatRequest().apply {
            this.message = text
            this.action = action
            this.actionId = actionId
            this.conversation = messages.map { AiChatMessageDto(it.text, it.isUser) }
        }

        coroutineScope.launch {
            isLoading = true
            try {
                val sessionManager = com.utc2.appreborn.utils.SessionManager.getInstance(context)
                val token = sessionManager.authToken
                val api = ApiClient.getInstance(token).create(AiChatApiService::class.java)
                val response = api.processMessage(request).awaitResponse()
                if (response.isSuccessful && response.body() != null) {
                    val res = response.body()!!
                    when (res.type) {
                        "answer" -> {
                            messages = messages + ChatMessage(
                                text = res.content ?: "",
                                isUser = false,
                                actionId = res.actionId,
                                documentTitle = res.documentTitle,
                                documentSource = res.documentSource,
                                confidenceScore = res.confidenceScore,
                                actionButtons = res.actionButtons
                            )
                            // Phục hồi lại gợi ý mặc định
                            suggestions = listOf(
                                ChatSuggestion("Xem lịch học ở đâu?"),
                                ChatSuggestion("Xem điểm thi"),
                                ChatSuggestion("Đăng ký tín chỉ")
                            )
                        }
                        "suggestions" -> {
                            suggestions = res.items?.map { ChatSuggestion(it) } ?: emptyList()
                        }
                        "clarify" -> {
                            messages = messages + ChatMessage(text = res.message ?: "", isUser = false)
                            suggestions = res.options?.map { ChatSuggestion(it) } ?: emptyList()
                        }
                        "not_found" -> {
                            messages = messages + ChatMessage(text = res.message ?: "", isUser = false)
                            suggestions = listOf(
                                ChatSuggestion("Xem lịch học ở đâu?"),
                                ChatSuggestion("Xem điểm thi"),
                                ChatSuggestion("Đăng ký tín chỉ")
                            )
                        }
                        "permission_required" -> {
                            messages = messages + ChatMessage(
                                text = res.message ?: "",
                                isUser = false,
                                actionButtons = res.actionButtons
                            )
                            suggestions = emptyList()
                        }
                        "calculation" -> {
                            messages = messages + ChatMessage(
                                text = "Kết quả tính toán: ${res.result}",
                                isUser = false
                            )
                            suggestions = listOf(
                                ChatSuggestion("Xem lịch học ở đâu?"),
                                ChatSuggestion("Xem điểm thi"),
                                ChatSuggestion("Đăng ký tín chỉ")
                            )
                        }
                    }
                } else {
                    messages = messages + ChatMessage("Máy chủ hiện đang quá tải", isUser = false)
                }
            } catch (e: Exception) {
                messages = messages + ChatMessage("Máy chủ hiện đang quá tải", isUser = false)
            } finally {
                isLoading = false
            }
        }
    }

    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        modifier = Modifier.padding(bottom = 80.dp),
        topBar = {
            TopAppBar(
                title = { Text("Trợ lý AI", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF5F5F5),
                    titleContentColor = Color.Black
                )
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                // Suggestions Row
                if (suggestions.isNotEmpty()) {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(suggestions) { suggestion ->
                            SuggestionChip(
                                onClick = { handleSend(suggestion.text) },
                                label = { Text(suggestion.text) },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = Color(0xFFF5F5F5)
                                ),
                                border = BorderStroke(1.dp, Color.LightGray)
                            )
                        }
                    }
                }

                // Input Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        placeholder = { Text("Nhập tin nhắn...") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            autoCorrect = false
                        ),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFF8F00),
                            unfocusedBorderColor = Color.LightGray
                        )
                    )
                    IconButton(
                        onClick = { handleSend(inputText.text) },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFF8F00))
                    ) {
                        Icon(
                            Icons.Default.Send,
                            contentDescription = "Gửi",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5F5)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages) { msg ->
                ChatBubble(
                    msg = msg,
                    chatActions = chatActions,
                    onNavigate = onNavigate,
                    onActionClick = { text, actionType, actionData -> 
                        handleSend(text, action = actionType, actionId = actionData)
                    }
                )
            }
            if (isLoading) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 48.dp, top = 8.dp, bottom = 8.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color(0xFFFF8F00),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Hệ thống đang trả lời...",
                            color = Color.Gray,
                            fontSize = 13.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(
    msg: ChatMessage,
    chatActions: Map<String, ActionConfig>,
    onNavigate: (String) -> Unit,
    onActionClick: (String, String?, String?) -> Unit
) {
    val backgroundColor = if (msg.isUser) Color(0xFFFFE082) else Color.White
    val textColor = Color.Black
    val alignment = if (msg.isUser) Alignment.CenterEnd else Alignment.CenterStart
    val shape = if (msg.isUser) {
        RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp)
    } else {
        RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp)
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = alignment
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            if (!msg.isUser) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_lucide_bot),
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier
                        .size(40.dp)
                        .padding(end = 4.dp)
                )
            }
            Box(
                modifier = Modifier
                    .widthIn(max = 280.dp)
                    .clip(shape)
                    .background(backgroundColor)
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        text = msg.text,
                        color = textColor,
                        fontSize = 15.sp
                    )
                    
                    val action = msg.actionId?.let { chatActions[it] }
                    if (action != null && !msg.isUser) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { onNavigate(action.route) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF81D4FA)),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(action.text, color = Color.Black, fontSize = 14.sp)
                        }
                    }

                    // Display Dynamic Action Buttons
                    if (!msg.actionButtons.isNullOrEmpty() && !msg.isUser) {
                        Spacer(modifier = Modifier.height(8.dp))
                        @OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
                        androidx.compose.foundation.layout.FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            msg.actionButtons.forEach { btn ->
                                val isNav = btn.type == "NAVIGATE"
                                val btnLabel = if (isNav) chatActions[btn.data]?.text ?: btn.label else btn.label
                                val btnColor = if (isNav) Color(0xFF81D4FA) else Color(0xFFE0E0E0)
                                Button(
                                    onClick = { 
                                        if (isNav) {
                                            chatActions[btn.data]?.route?.let { onNavigate(it) }
                                        } else if (btn.type == "SUGGESTED_QUESTION") {
                                            onActionClick(btn.data, null, null)
                                        } else if (btn.type == "GRANT_PERMISSION" || btn.type == "DENY_PERMISSION") {
                                            onActionClick(btn.label, btn.type, btn.data)
                                        } else {
                                            onActionClick("", btn.type, btn.data)
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = btnColor),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                ) {
                                    Text(btnLabel, color = Color.Black, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
