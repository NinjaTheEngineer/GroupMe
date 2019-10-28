package ninja.engineer.GroupMeProjetoFinal;

import com.backendless.messaging.DeliveryOptions;
import com.backendless.messaging.MessageStatus;
import com.backendless.messaging.PublishOptions;
import com.backendless.servercode.ExecutionResult;
import com.backendless.servercode.RunnerContext;
import com.backendless.servercode.annotation.Asset;

@Asset("*")
public class SaveMessages extends com.backendless.servercode.extension.MessagingExtender {
    private String message;
    private String channelName;
    private String publisher;
    private Boolean samePub;

    public SaveMessages(){
    }

    @Override
    public void afterPublish(RunnerContext context, Object Message, PublishOptions publishOptions, DeliveryOptions deliveryOptions, ExecutionResult<MessageStatus> status) throws Exception {
        message = Message.toString();
        publisher = ApplicationClass.userName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getPublisher() {
        return publisher;
    }

    public Boolean getSamePub() {
        if(getPublisher().equals(ApplicationClass.userName)){
            samePub = true;
        }else{
            samePub = false;
        }
        return samePub;
    }

    public void setSamePub(Boolean samePub) {
        this.samePub = samePub;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
}
