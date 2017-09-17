package eu.bittrade.libs.steemj.base.models.operations;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;

import eu.bittrade.libs.steemj.annotations.SignatureRequired;
import eu.bittrade.libs.steemj.base.models.AccountName;
import eu.bittrade.libs.steemj.base.models.Price;
import eu.bittrade.libs.steemj.enums.OperationType;
import eu.bittrade.libs.steemj.enums.PrivateKeyType;
import eu.bittrade.libs.steemj.exceptions.SteemInvalidTransactionException;
import eu.bittrade.libs.steemj.interfaces.SignatureObject;
import eu.bittrade.libs.steemj.util.SteemJUtils;

/**
 * This class represents the Steem "feed_publish_operation" object.
 * 
 * @author <a href="http://steemit.com/@dez1337">dez1337</a>
 */
public class FeedPublishOperation extends Operation {
    @SignatureRequired(type = PrivateKeyType.ACTIVE)
    @JsonProperty("publisher")
    private AccountName publisher;
    @JsonProperty("exchange_rate")
    private Price exchangeRate;

    /**
     * Create a new feed publish operation. Feeds can only be published by the
     * top N witnesses which are included in every round and are used to define
     * the exchange rate between steem and the dollar.
     */
    public FeedPublishOperation() {
        super(false);
    }

    /**
     * Get the account name of the witness that published a new price feed.
     * 
     * @return The account name of the witness that published a new price feed.
     */
    public AccountName getPublisher() {
        return publisher;
    }

    /**
     * Set the account name of the witness that will publish a new price feed.
     * <b>Notice:</b> The private active key of this account needs to be stored
     * in the key storage.
     * 
     * @param publisher
     *            The account name of the witness that will publish a new price
     *            feed.
     */
    public void setPublisher(AccountName publisher) {
        this.publisher = publisher;
    }

    /**
     * Get the exchange rate suggested by the witness.
     * 
     * @return The exchange rate suggested by the witness.
     */
    public Price getExchangeRate() {
        return exchangeRate;
    }

    /**
     * Set the exchange rate suggested by the witness.
     * 
     * @param exchangeRate
     *            The exchange rate suggested by the witness.
     */
    public void setExchangeRate(Price exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    @Override
    public byte[] toByteArray() throws SteemInvalidTransactionException {
        try (ByteArrayOutputStream serializedFeedPublishOperation = new ByteArrayOutputStream()) {
            serializedFeedPublishOperation
                    .write(SteemJUtils.transformIntToVarIntByteArray(OperationType.FEED_PUBLISH_OPERATION.ordinal()));
            serializedFeedPublishOperation.write(this.getPublisher().toByteArray());
            serializedFeedPublishOperation.write(this.getExchangeRate().toByteArray());

            return serializedFeedPublishOperation.toByteArray();
        } catch (IOException e) {
            throw new SteemInvalidTransactionException(
                    "A problem occured while transforming the operation into a byte array.", e);
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
    
    @Override
    public Map<SignatureObject, List<PrivateKeyType>> getRequiredAuthorities(
            Map<SignatureObject, List<PrivateKeyType>> requiredAuthoritiesBase) {
        return mergeRequiredAuthorities(requiredAuthoritiesBase, this.getOwner(), PrivateKeyType.ACTIVE);
    }
}
