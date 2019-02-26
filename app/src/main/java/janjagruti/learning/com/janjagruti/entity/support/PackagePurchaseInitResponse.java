package janjagruti.learning.com.janjagruti.entity.support;

import janjagruti.learning.com.janjagruti.entity.Package;
import janjagruti.learning.com.janjagruti.entity.User;

public class PackagePurchaseInitResponse {

    private String txnId;
    private User customer;
    private Package product;

    private String key;
    private String merchantId;
    private String hash;

    public String getTxnId() {
        return txnId;
    }

    public void setTxnId(String txnId) {
        this.txnId = txnId;
    }

    public User getCustomer() {
        return customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }

    public Package getProduct() {
        return product;
    }

    public void setProduct(Package product) {
        this.product = product;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    @Override
    public String toString() {
        return "PackagePurchaseInitResponse{" +
                "txnId='" + txnId + '\'' +
                ", customer=" + customer +
                ", product=" + product +
                ", key='" + key + '\'' +
                ", merchantId='" + merchantId + '\'' +
                ", hash='" + hash + '\'' +
                '}';
    }
}
