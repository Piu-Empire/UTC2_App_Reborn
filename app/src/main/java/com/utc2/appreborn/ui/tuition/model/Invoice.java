package com.utc2.appreborn.ui.tuition.model;

/**
 * Invoice — đại diện 1 hóa đơn đã thanh toán.
 * type = TUITION : học phí môn học (bảng fee)
 * type = DORM    : phí KTX (bảng dormitory_registration)
 */
public class Invoice {

    public enum Type { TUITION, DORM }

    private Type    type;
    private String  invoiceCode;
    private long    refId;         // fee_id hoặc dorm_reg_id
    private long    semesterId;    // chỉ có ở TUITION, 0 nếu DORM
    private String  label;         // VD: "Học kỳ 1" hoặc "KTX A · A-201"
    private String  paidAt;
    private String  paymentMethod;
    private double  totalAmount;
    private double  paidAmount;

    public Invoice(Type type, String invoiceCode, long refId, long semesterId,
                   String label, String paidAt, String paymentMethod,
                   double totalAmount, double paidAmount) {
        this.type          = type;
        this.invoiceCode   = invoiceCode;
        this.refId         = refId;
        this.semesterId    = semesterId;
        this.label         = label;
        this.paidAt        = paidAt;
        this.paymentMethod = paymentMethod;
        this.totalAmount   = totalAmount;
        this.paidAmount    = paidAmount;
    }

    public Type   getType()          { return type; }
    public String getInvoiceCode()   { return invoiceCode; }
    public long   getRefId()         { return refId; }
    public long   getSemesterId()    { return semesterId; }
    public String getLabel()         { return label; }
    public String getPaidAt()        { return paidAt; }
    public String getPaymentMethod() { return paymentMethod; }
    public double getTotalAmount()   { return totalAmount; }
    public double getPaidAmount()    { return paidAmount; }
}