package ninja.engineer.GroupMeProjetoFinal;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.messaging.MessageStatus;
import com.backendless.persistence.DataQueryBuilder;
import com.backendless.rt.messaging.Channel;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFrag extends Fragment{

    public static final String TAG = "RTChat";
    private EditText message;
    private TextView messages;
    private Channel channel;
    private String color = ColorPicker.next();
    private String name, channelName;
    private String publisher;

    View v;

    public ChatFrag() {

     }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        v = getView();
        message = v.findViewById(R.id.message);
        messages = v.findViewById(R.id.messages);


        name = getArguments().getString("userName");
        channelName = ApplicationClass.userCurrentChannel;
        //Adicionar o utilizar ao channel do grupo
        channel = Backendless.Messaging.subscribe(channelName);

        //Ativar o chat, o user consegue assim ver as mensqens do chat
        channel.addJoinListener(new AsyncCallback<Void>() {
            @Override
            public void handleResponse(Void response) {
                Backendless.Messaging.publish(channelName, wrapToColor(name) + " entrou no chat.", new AsyncCallback<MessageStatus>() {
                    @Override
                    public void handleResponse(MessageStatus response) {
                        Log.d(TAG, "Sent joined " + response);
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        ChatFrag.this.handleFault(fault);
                    }
                });
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                ChatFrag.this.handleFault(fault);
            }
        });

        //Adicionar as mensanges ao ecrã de mensagens do layout.
        channel.addMessageListener(new AsyncCallback<String>() {
            @Override
            public void handleResponse(String response) {
                messages.append(Html.fromHtml("<br/>" + response));
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                ChatFrag.this.handleFault(fault);
            }
        });
        //Função de Publicação de mensagem
        message.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;

                if (actionId == EditorInfo.IME_ACTION_SEND || event.getKeyCode() == KeyEvent.KEYCODE_ENTER && message.getText().toString().trim().length() > 0) {
                    message.setEnabled(false);
        //Publicar mensagem
                    Backendless.Messaging.publish(channelName, wrapToColor("[" + name + "]") + ": " + message.getText().toString(), new AsyncCallback<MessageStatus>() {
                        @Override
                        public void handleResponse(MessageStatus response) {
                            Log.d(TAG, "Sent message " + response);

                            SaveMessages messageObject = new SaveMessages();
                            messageObject.setMessage(message.getText().toString());
                            messageObject.setChannelName(channelName);
                            messageObject.setPublisher(ApplicationClass.userName);
        //Guardar a mensagem como um objecto para que possa ser retomada
                            Backendless.Data.of(SaveMessages.class).save(messageObject, new AsyncCallback<SaveMessages>() {
                                @Override
                                public void handleResponse(SaveMessages response) {
                                    Toast.makeText(getContext(), "Message Saved", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void handleFault(BackendlessFault fault) {
                                    Toast.makeText(getContext(), "Message Fail", Toast.LENGTH_SHORT).show();
                                }
                            });

                            message.setText("", TextView.BufferType.EDITABLE);
                            message.setEnabled(true);
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            message.setEnabled(true);
                        }
                    });


                    handled = true;
                }

                return handled;
            }
        });

    }

    private void handleFault(BackendlessFault fault) {
        Log.e(TAG, fault.toString());
    }

    private String wrapToColor(String value) {
        return "<font color='" + color + "'>" + value + "</font>";
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_chat, container, false);
        retrieveMessageHistory();
        return v;
    }
        //Função para o utilizador se desconetar do channel ativo
    public void disconnectChannel(){
        channel.removeMessageListener(new AsyncCallback<String>() {
            @Override
            public void handleResponse(String response) {

            }

            @Override
            public void handleFault(BackendlessFault fault) {

            }
        });
    }
        //Função para retomar da base de dados Backendless as mensagens antigas
    public void retrieveMessageHistory(){
        channelName = ApplicationClass.userCurrentChannel;
        DataQueryBuilder dataQueryBuilder = DataQueryBuilder.create();
        dataQueryBuilder.setOffset(0);
        dataQueryBuilder.setPageSize(100);
        dataQueryBuilder.setSortBy("created");

        String whereClause = "channelName = '" + channelName + "'";
        dataQueryBuilder.setWhereClause(whereClause);

        Backendless.Data.of(SaveMessages.class).find(dataQueryBuilder, new AsyncCallback<List<SaveMessages>>() {
            @Override
            public void handleResponse(List<SaveMessages> response) {
                for(SaveMessages s: response){
                    if(s.getPublisher().equals(name)){
                        messages.append(Html.fromHtml("<br/>" + wrapToColor("[Eu]") + ": " + s.getMessage()));
                    }else{
                        messages.append(Html.fromHtml("<br/>" + wrapToColor("[" + s.getPublisher() + "]") + ": " + s.getMessage()));
                    }
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Toast.makeText(getContext(), "Errou", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
