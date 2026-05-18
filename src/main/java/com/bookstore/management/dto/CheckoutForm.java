// CHECKOUT FORM DTO - U.L.G.Maduwantha - IT19010618
// Captures customer input during checkout process
package com.bookstore.management.dto; import jakarta.validation.constraints.NotBlank;
public class CheckoutForm
{
    // FIELDS
    // shippingAddress - required, cannot be blank
    // note            - optional customer note

    @NotBlank(message = "Shipping address is required") private String shippingAddress; private String note;

    // SECTION 2: GETTERS AND SETTERS
    // Get and set shipping address and note

    public String getShippingAddress()  {
        return shippingAddress;}
    public void setShippingAddress(String shippingAddress) {this.shippingAddress = shippingAddress;}
    public String getNote() {return note;}
    public void setNote(String note) {this.note = note;}}
