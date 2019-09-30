//package com.taxi.nanny;
//
//
//import android.annotation.SuppressLint;
//import android.content.ContentResolver;
//import android.content.ContentUris;
//import android.content.Context;
//import android.content.Intent;
//import android.database.Cursor;
//import android.database.DatabaseUtils;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Color;
//import android.net.Uri;
//import android.os.AsyncTask;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Environment;
//import android.os.Handler;
//import android.provider.DocumentsContract;
//import android.provider.MediaStore;
//import android.provider.OpenableColumns;
//import android.support.annotation.Nullable;
//import android.support.v4.content.FileProvider;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.text.Editable;
//import android.text.TextUtils;
//import android.text.TextWatcher;
//import android.util.Log;
//import android.view.GestureDetector;
//import android.view.KeyEvent;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.inputmethod.EditorInfo;
//import android.view.inputmethod.InputMethodManager;
//import android.webkit.MimeTypeMap;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.squareup.picasso.Picasso;
//import com.theartofdev.edmodo.cropper.CropImage;
//
//import org.apache.commons.io.FilenameUtils;
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.OutputStream;
//import java.io.OutputStreamWriter;
//import java.net.URISyntaxException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Locale;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//import butterknife.OnClick;
//import de.hdodenhof.circleimageview.CircleImageView;
//import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
//import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
//import io.socket.client.Ack;
//import io.socket.client.IO;
//import io.socket.client.Socket;
//import io.socket.emitter.Emitter;
//import youcatch.promatics.com.youcatch.BuildConfig;
//import youcatch.promatics.com.youcatch.R;
//import youcatch.promatics.com.youcatch.chat.adapter.SocialChatAdapter;
//import youcatch.promatics.com.youcatch.chat.model.ChatModel;
//import youcatch.promatics.com.youcatch.retrofit.RetrofitResponse;
//import youcatch.promatics.com.youcatch.retrofit.RetrofitService;
//import youcatch.promatics.com.youcatch.retrofit.WebUrls;
//import youcatch.promatics.com.youcatch.sharedPreferences.CommonKeys;
//import youcatch.promatics.com.youcatch.sharedPreferences.SharedPreference;
//import youcatch.promatics.com.youcatch.utils.AppController;
//import youcatch.promatics.com.youcatch.utils.CommonToolbar;
//import youcatch.promatics.com.youcatch.utils_sweet_alert.UtilityClass;
//
//public class SocialChat extends CommonToolbar implements RetrofitResponse {
//
//    @BindView(R.id.etWriteTextMsg)
//    EditText etWriteTextMsg;
//    @BindView(R.id.ivSendIcon)
//    ImageView ivSendIcon;
//    @BindView(R.id.iv_Home)
//    ImageView iv_Home;
//    @BindView(R.id.imageView)
//    CircleImageView imageView;
//    @BindView(R.id.rv_Chat)
//    RecyclerView rv_Chat;
//    @BindView(R.id.rl_Home)
//    RelativeLayout rl_Home;
//    @BindView(R.id.rl_UserSearch)
//    RelativeLayout rl_UserSearch;
//    @BindView(R.id.rl_Gallery)
//    RelativeLayout rl_Gallery;
//    @BindView(R.id.rl_Attachment)
//    RelativeLayout rl_Attachment;
//    @BindView(R.id.rootView)
//    RelativeLayout rootView;
//    Socket mSocket;
//    String receiverId = "", receiverName = "", receiverImage = "";
//    private Context context = this;
//    String roomId = "";
//    private ArrayList<ChatModel> chatModels = new ArrayList<>();
//    private Boolean isConnected = true, mTyping = false;
//    private Handler mTypingHandler = new Handler();
//    SocialChatAdapter adapter;
//    private final int CAMERA_CODE = 12;
//    private final int GALLERY_CODE = 13;
//    private final int REQUEST_CODE_DOC = 14;
//    private Bitmap bitmap;
//    private File chatImage;
//    private String imgPath;
//    private HashMap<String, String> media_path = new HashMap<>();
//    private String SEND_MEDIA = "", type = "";
//    private String TAG = getClass().getSimpleName();
//    boolean sendPath = true;
//    private Uri imageToUploadUri;
//    youcatch.promatics.com.youcatch.utils.emoji.EmojIconActions emojIcon;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_social_chat);
//
//        ButterKnife.bind(this);
//
//        receiverId = getIntent().getStringExtra(CommonKeys.msgUserId);
//        receiverName = getIntent().getStringExtra(CommonKeys.msgUserName);
//        receiverImage = getIntent().getStringExtra(CommonKeys.msgUserImage);
//        getChatListService(receiverId);
//        Picasso.with(context)
//                .load(WebUrls.PROFILE_IMAGES + receiverImage)
//                .placeholder(R.drawable.user_placeholder)
//                .into(imageView);
//        connectSocket();
//        tvtitle.setText(receiverName);
//        tvtitle.setTextColor(Color.BLACK);
//        ivBack1.setVisibility(View.VISIBLE);
//        ivBack.setVisibility(View.GONE);
//        rlUserImage.setVisibility(View.VISIBLE);
//        ivStoryUpdate.setVisibility(View.GONE);
//        rlayout.setBackgroundColor(Color.WHITE);
//        sendMessage();
//
//        etWriteTextMsg.setOnTouchListener(new View.OnTouchListener()
//        {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                rlBelowLayout_gesture.onTouchEvent(event);
//                return false;
//            }
//        });
//
//        emojIcon = new youcatch.promatics.com.youcatch.utils.emoji.EmojIconActions(this, rootView, etWriteTextMsg,
//                iv_Home);
//        emojIcon.ShowEmojIcon();
//        emojIcon.setKeyboardListener(new EmojIconActions.KeyboardListener()
//        {
//            @Override
//            public void onKeyboardOpen()
//            {
//                Log.e("Keyboard", "open");
//            }
//            @Override
//            public void onKeyboardClose()
//            {
//                Log.e("Keyboard", "close");
//            }
//        });
//        if (etWriteTextMsg.length() == 0)
//            ivSendIcon.setVisibility(View.GONE); //disable at app start
//    }
//
//    @OnClick({R.id.ivSendIcon, R.id.rl_Home, R.id.rl_UserSearch, R.id.rl_Gallery, R.id.rl_Attachment})
//    void onclick(View v) {
//        switch (v.getId()) {
//            case R.id.rl_UserSearch:
//                startCamera();
//                break;
//            case R.id.rl_Home:
//
//                break;
//            case R.id.rl_Gallery:
//                pickGalleryData();
//                break;
//            case R.id.ivSendIcon:
//                attemptSend();
//                break;
//            case R.id.rl_Attachment:
//                browseDocuments();
//                break;
//        }
//    }
//
//    private void pickGalleryData() {
//        Intent pickPhoto = new Intent(Intent.ACTION_PICK);
//        pickPhoto.setType("*/*");
//        startActivityForResult(pickPhoto, GALLERY_CODE);
//    }
//
//    private void startCamera() {
//        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        cameraIntent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
//        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
//            try {
//                @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//                chatImage = new File(AppController.getIMAGE(), timeStamp + "image.png");
//                Uri photoURI = FileProvider.getUriForFile(context, youcatch.promatics.com
//                        .youcatch.BuildConfig.APPLICATION_ID + ".provider", chatImage);
//                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//                startActivityForResult(cameraIntent, CAMERA_CODE);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private void sendMessage() {
//        etWriteTextMsg.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
//                if (i == R.id.ivSendIcon || i == EditorInfo.IME_NULL) {
//                    attemptSend();
//                    return true;
//                }
//                return false;
//            }
//        });
//        etWriteTextMsg.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void afterTextChanged(Editable s) {
//                //txt.setText(message_body.length() + " / 160"); //This is my textwatcher to update character left in my EditText
//                if (etWriteTextMsg.length() == 0)
//                    ivSendIcon.setVisibility(View.GONE); //disable send button if no text entered
//                else
//                    ivSendIcon.setVisibility(View.VISIBLE);  //otherwise enable
//
//                if (!mSocket.connected())
//                    return;
//
//
//                if (!mTyping) {
//                    mTyping = true;
//                    try {
//                        JSONObject typingInObj = new JSONObject();
//                        typingInObj.put("room_id", roomId);
//                        typingInObj.put("sender_id", SharedPreference.retrieveKey(context, CommonKeys.KEY_ID));
//                        //todo emit for typing status
//                        mSocket.emit("typeIn", typingInObj);
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                mTypingHandler.removeCallbacks(onTypingTimeout);
//                mTypingHandler.postDelayed(onTypingTimeout, 600);
//
//            }
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//            }
//        });
//    }
//
//    private Runnable onTypingTimeout = new Runnable() {
//        @Override
//        public void run() {
//            if (!mTyping) return;
//            mTyping = false;
//            callTypingTimeOutEmit();
//        }
//    };
//
//    private void callTypingTimeOutEmit() {
//        JSONObject param = new JSONObject();
//        try {
//            param.put("room_id", roomId);
//            param.put("sender_id", SharedPreference.retrieveKey(context, CommonKeys.KEY_ID));
//            mSocket.emit("typeOut", param);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//    // send message
//    private void attemptSend() {
//        if (!mSocket.connected()) return;
//        String message = etWriteTextMsg.getText().toString().trim();
//        if (TextUtils.isEmpty(message)) {
//            etWriteTextMsg.requestFocus();
//            return;
//        }
//
//        JSONObject object = new JSONObject();
//        try {
//            /*room_id,sender_id,receiver_id,unique_code,message*/
//            String unique_code = String.valueOf(System.currentTimeMillis());
//            object.put("room_id", roomId);
//            object.put("message", etWriteTextMsg.getText().toString().trim());
//            object.put("sender_id", SharedPreference.retrieveKey(context, CommonKeys.KEY_ID));
//            object.put("receiver_id", receiverId);
//            object.put("unique_code", unique_code);
//            mSocket.emit("message", object, new Ack() {
//                @Override
//                public void call(Object... args) {
//                    JSONObject object1 = (JSONObject) args[0];
//                    Log.e("SocialChat", "call:message " + object1.toString());
//                }
//            });
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        etWriteTextMsg.setText("");
//    }
//
//    //todo to scroll recycler to bottom when edit text is focused...
//    final GestureDetector rlBelowLayout_gesture = new GestureDetector
//            (new GestureDetector.OnGestureListener() {
//
//                @Override
//                public boolean onDown(MotionEvent e) {
//                    return false;
//                }
//
//                @Override
//                public void onShowPress(MotionEvent e) {
//
//                }
//
//                @Override
//                public boolean onSingleTapUp(MotionEvent e) {
//                    try {
//
//                        etWriteTextMsg.requestFocus();
//                        UtilityClass.showKeyBoard(SocialChat.this, etWriteTextMsg);
//                        // Alerts.showKeyboard(ChatActivity.this, etMsg);
//                        if (Build.VERSION.SDK_INT >= aa) {
//
//                            if (chatModels.size() > 2) {
//                                rv_Chat.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
//                                    @Override
//                                    public void onLayoutChange(View v,
//                                                               int left, int top, int right, int bottom,
//                                                               int oldLeft, int oldTop, int oldRight, int oldBottom) {
//                                        if (bottom < oldBottom) {
//                                            rv_Chat.postDelayed(new Runnable() {
//                                                @Override
//                                                public void run() {
//                                                    rv_Chat.smoothScrollToPosition(
//                                                            rv_Chat.getAdapter().getItemCount() - 1);
//                                                }
//                                            }, 100);
//                                        }
//                                    }
//                                });
//                            }
//                        }
//                    } catch (Exception qe) {
//                        qe.printStackTrace();
//
//                    }
//                    return false;
//                }
//
//                @Override
//                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//
//                    return false;
//                }
//
//                @Override
//                public void onLongPress(MotionEvent e) {
//
//                }
//
//                @Override
//                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//                    if (e1.getY() < e2.getY()) {
//                        //etMsg.setFocusable(false);
//                        etWriteTextMsg.clearFocus();
//                        try {
//                            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
//                            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
////                Alerts.hideKeyboard(ChatActivity.this, etMsg);
//                        return true;
//                    }
//
//                    return false;
//                }
//
//            });
//
//
//    private void getChatListService(String receiverId) {
//        JSONObject param = new JSONObject();
//        try {
//            /*user_id,friend_id*/
//            param.put("user_id", SharedPreference.retrieveKey(context, CommonKeys.KEY_ID));
//            param.put("friend_id", receiverId);
//            new RetrofitService(context, this, WebUrls.MESSAGE_LIST, param
//                    , WebUrls.REQ_MESSAGE_LIST, 2).callService(true);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    private void connectSocket() {
//        try {
//            mSocket = IO.socket(WebUrls.SOCKET_URL);
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
//        mSocket.on(Socket.EVENT_CONNECT, onConnect);
//        mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
//        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectionTimeOut);
//        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectionError);
//        mSocket.on("room join", onRoomJoin);
//        mSocket.on("typeIn", onTyping);
//        mSocket.on("typeOut", onTypeOut);
//        mSocket.on("message", onNewMessage);
//        mSocket.on("message read", onMessageRead);
//        mSocket.on("room leave", onRoomLeave);
//        mSocket.on("uploadFileCompleteRes", fileUploadCompleted);
//        mSocket.connect();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        disconnectSocket();
//    }
//
//    private void disconnectSocket() {
//        roomLeave();
//        mSocket.off(Socket.EVENT_CONNECT, onConnect);
//        mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
//        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectionTimeOut);
//        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectionError);
//        mSocket.off("room join", onRoomJoin);
//        mSocket.off("typeIn", onTyping);
//        mSocket.off("typeOut", onTypeOut);
//        mSocket.off("message", onNewMessage);
//        mSocket.off("message read", onMessageRead);
//        mSocket.off("room leave", onRoomLeave);
//        mSocket.off("uploadFileCompleteRes", fileUploadCompleted);
//        mSocket.disconnect();
//    }
//
//    private Emitter.Listener fileUploadCompleted = new Emitter.Listener() {
//        @Override
//        public void call(final Object... args) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    JSONObject object = (JSONObject) args[0];
//
//                    Log.e("fileUploadComplted", ">>>>>>>>>>> " + object.toString());
//                }
//            });
//        }
//    };
//
//    private void roomLeave() {
//        JSONObject object = new JSONObject();
//        try {
//            object.put("room_id", roomId);
//            object.put("sender_id", SharedPreference.retrieveKey(context, CommonKeys.KEY_ID));
//            mSocket.emit("room leave", object, new Ack() {
//                @Override
//                public void call(Object... args) {
//                    JSONObject object1 = (JSONObject) args[0];
//                    Log.e("RoomLeft", "roomLeft-> " + object1.toString());
//                }
//            });
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private Emitter.Listener onConnect = new Emitter.Listener() {
//        @Override
//        public void call(Object... args) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    if (!isConnected) {
//                        Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show();
//                        isConnected = true;
//                    }
//                }
//            });
//        }
//    };
//    private Emitter.Listener onDisconnect = new Emitter.Listener() {
//        @Override
//        public void call(Object... args) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    isConnected = false;
//                    Toast.makeText(SocialChat.this, "DisconnectConnect!", Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//    };
//    private Emitter.Listener onConnectionTimeOut = new Emitter.Listener() {
//        @Override
//        public void call(Object... args) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Toast.makeText(SocialChat.this, "onConnectionTimeOut!", Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//    };
//    private Emitter.Listener onConnectionError = new Emitter.Listener() {
//        @Override
//        public void call(Object... args) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Toast.makeText(SocialChat.this, "onConnectionError!", Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//    };
//    private Emitter.Listener onRoomJoin = new Emitter.Listener() {
//        @Override
//        public void call(final Object... args) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    JSONObject object = (JSONObject) args[0];
//                    Log.e("SocialChat", "run:onRoomJoin " + object.toString());
//                }
//            });
//        }
//    };
//
//    private Emitter.Listener onTyping = new Emitter.Listener() {
//        @Override
//        public void call(final Object... args) {
//            final JSONObject object = (JSONObject) args[0];
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        if (!object.getString("sender_id").equalsIgnoreCase(SharedPreference.retrieveKey(
//                                context, CommonKeys.KEY_ID))) {
//                            tvtitle.setText(receiverName + " is Typing...");
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    Log.e("SocialChat", "run:onTyping " + object.toString());
//                }
//            });
//        }
//    };
//
//    private Emitter.Listener onTypeOut = new Emitter.Listener() {
//        @Override
//        public void call(Object... args) {
//            final JSONObject object = (JSONObject) args[0];
//
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//
//                    try {
//                        if (object.getString("sender_id").equalsIgnoreCase
//                                (receiverId)) {
//                            tvtitle.setText(receiverName);
//                        }
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                    Log.e("SocialChat", "run:onTypeOut " + object.toString());
//
//                }
//            });
//        }
//    };
//
//    private Emitter.Listener onNewMessage = new Emitter.Listener() {
//        @Override
//        public void call(Object... args) {
//            final JSONObject object = (JSONObject) args[0];
//
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    /*{"room_id":"99660097066135","message":"we","sender_id":"12","receiver_id":"135"
//                    ,"unique_code":"1560160235450","created_at":"2019-06-10T09:50:36.142Z"}*/
//
//                   /*{"Name":"IMG_20190611_115432.jpg","Size":2325,"room_id":"99660097066135","sender_id":"12",
//                   "receiver_id":"135","message":"Attachment","unique_code":"1560236463774",
//                   "attachment_type":"image","Data":"IMG_20190611_115432.jpg","attachment":"IMG_20190611_115432.jpg",
//                   "check_status":"unread","created_at":"2019-06-11T07:01:18.010Z","read_time":null,
//                   "full_message":{"id":855,"sender_id":"12","receiver_id":"135",
//                   "message":"IMG_20190611_115432.jpg","room_id":"99660097066135","message_type":"media"}}*/
//                    Log.e("SocialChat", "run:onNewMessage " + object.toString());
//                    try {
//                        if (SharedPreference.retrieveKey(context, CommonKeys.KEY_ID)
//                                .equalsIgnoreCase(object.getString("sender_id"))) {
//                            if (object.has("full_message")) {
//                                JSONObject fullMsgObj = object.getJSONObject("full_message");
//                                ChatModel model = new ChatModel();
//                                model.setRoom_id(object.getString("room_id"));
//                                model.setMessage(fullMsgObj.getString("message"));
//                                model.setReceiver_id(object.getString("receiver_id"));
//                                model.setSender_id(object.getString("sender_id"));
//                                model.setUnique_code(object.getString("unique_code"));
//                                model.setAttachment(object.getString("attachment"));
//                                model.setAttachment_Type(object.getString("attachment_type"));
//                                model.setReceiver_image(receiverImage);
//                                model.setSendNow("0");
//                                model.setTime(object.getString("created_at"));
//                                model.setMessage_type(fullMsgObj.getString("message_type"));
//                                model.setCheck_status(object.getString("check_status"));
//                                chatModels.add(model);
//                                Log.e(TAG, "run: " + "data");
//
//                            } else {
//
//                                ChatModel model = new ChatModel();
//                                model.setRoom_id(object.getString("room_id"));
//                                model.setMessage(object.getString("message"));
//                                model.setReceiver_id(object.getString("receiver_id"));
//                                model.setSender_id(object.getString("sender_id"));
//                                model.setUnique_code(object.getString("unique_code"));
//                                model.setReceiver_image(receiverImage);
//                                model.setTime(object.getString("created_at"));
//                                model.setMessage_type("normal");
//                                model.setSendNow("0");
//                                model.setCheck_status("unread");
//                                chatModels.add(model);
//                            }
//                            for (int i = 0; i < chatModels.size(); i++) {
//                                if (chatModels.get(i).getSendNow().equalsIgnoreCase("2")) {
//                                    chatModels.remove(i);
//                                }
//                            }
//                            if (adapter == null) {
//                                setChatAdapter();
//                            } else {
//                                adapter.notifyDataSetChanged();
//                                smothScroll();
//                            }
//                        } else {
//                            if (object.has("full_message")) {
//                                JSONObject fullMsgObj = object.getJSONObject("full_message");
//                                ChatModel model = new ChatModel();
//                                model.setRoom_id(object.getString("room_id"));
//                                model.setMessage(fullMsgObj.getString("message"));
//                                model.setReceiver_id(object.getString("receiver_id"));
//                                model.setSender_id(object.getString("sender_id"));
//                                model.setUnique_code(object.getString("unique_code"));
//                                model.setReceiver_image(receiverImage);
//                                model.setTime(object.getString("created_at"));
//                                model.setSendNow("0");
//                                model.setAttachment(object.getString("attachment"));
//                                model.setAttachment_Type(object.getString("attachment_type"));
//                                model.setMessage_type(fullMsgObj.getString("message_type"));
//                                model.setCheck_status(object.getString("check_status"));
//                                chatModels.add(model);
//
//                            } else {
//                                ChatModel model = new ChatModel();
//                                model.setRoom_id(object.getString("room_id"));
//                                model.setMessage(object.getString("message"));
//                                model.setReceiver_id(object.getString("receiver_id"));
//                                model.setSender_id(object.getString("sender_id"));
//                                model.setUnique_code(object.getString("unique_code"));
//                                model.setReceiver_image(receiverImage);
//                                model.setSendNow("0");
//                                model.setTime(object.getString("created_at"));
//                                model.setMessage_type("normal");
//                                model.setCheck_status("unread");
//                                chatModels.add(model);
//                            }
//                            if (adapter == null) {
//                                setChatAdapter();
//                            } else {
//                                adapter.notifyDataSetChanged();
//                                smothScroll();
//                            }
//                            emitMessageRead(object.getString("room_id"), object.getString("unique_code"));
//
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//        }
//    };
//
//    private Emitter.Listener onMessageRead = new Emitter.Listener() {
//        @Override
//        public void call(Object... args) {
//            JSONObject object = (JSONObject) args[0];
//
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//
//                }
//            });
//        }
//    };
//
//    private Emitter.Listener onRoomLeave = new Emitter.Listener() {
//        @Override
//        public void call(Object... args) {
//            final JSONObject object = (JSONObject) args[0];
//
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Log.e("SocialChat", "run:onRoomLeave " + object.toString());
//                }
//            });
//        }
//    };
//
//
//    @Override
//    public void onResponse(int RequestCode, String response) {
//        try {
//            JSONObject object = new JSONObject(response);
//            boolean status = object.getBoolean("response");
//            roomId = object.getString("room_id");
//            joinRoom(roomId);
//            switch (RequestCode) {
//                case WebUrls.REQ_MESSAGE_LIST:
//                    if (status) {
//                        JSONArray dataArray = object.getJSONArray("data");
//                        chatModels.clear();
//                        for (int i = 0; i < dataArray.length(); i++) {
//                            JSONObject dataObj = dataArray.getJSONObject(i);
//                            ChatModel model = new ChatModel();
//                            model.setChat_id(dataObj.getString("id"));
//                            model.setRoom_id(dataObj.getString("room_id"));
//                            model.setSender_id(dataObj.getString("sender_id"));
//                            model.setReceiver_id(dataObj.getString("receiver_id"));
//                            model.setMessage(dataObj.getString("message"));
//                            model.setAttachment_Type(dataObj.getString("attachment_type"));
//                            model.setMessage_type(dataObj.getString("message_type"));
//                            model.setUnique_code(dataObj.getString("unique_code"));
//                            model.setCheck_status(dataObj.getString("check_status"));
//                            model.setTime(dataObj.getString("created_at"));
//                            model.setReceiver_image(receiverImage);
//                            model.setSendNow("0");
//                            chatModels.add(model);
//                            if (dataObj.getString("receiver_id").
//                                    equalsIgnoreCase(SharedPreference.retrieveKey(context, CommonKeys.KEY_ID))) {
//
//                                emitMessageRead(dataObj.getString("room_id"), dataObj.getString("unique_code"));
//                            }
//                        }
//                        setChatAdapter();
//                    } else {
//
//                    }
//
//                    break;
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//    // message read
//    private void emitMessageRead(String room_id, String unique_code) {
//        JSONObject object = new JSONObject();
//        try {
//            object.put("room_id", room_id);
//            object.put("unique_code", unique_code);
//            mSocket.emit("message read", object, new Ack() {
//                @Override
//                public void call(final Object... args) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            JSONObject object1 = (JSONObject) args[0];
//                            Log.e("emitMessageRead", "MessageRead " + object1);
//                        }
//                    });
//
//                }
//            });
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void joinRoom(String roomId) {
//        JSONObject param = new JSONObject();
//        /*room_id,sender_id,*/
//        try {
//            param.put("room_id", roomId);
//            param.put("sender_id", SharedPreference.retrieveKey(context, CommonKeys.KEY_ID));
//            mSocket.emit("room join", param, new Ack() {
//                @Override
//                public void call(final Object... args) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            JSONObject object = (JSONObject) args[0];
//                            Log.e("SocialChat", "run: " + object.toString());
//                        }
//                    });
//                }
//            });
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void setChatAdapter() {
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
//
//        if (chatModels.size() > 10) {
//            linearLayoutManager.setStackFromEnd(true);
//            rv_Chat.setLayoutManager(linearLayoutManager);
//            adapter = new SocialChatAdapter(context, chatModels);
//            smothScroll();
//        } else {
//            rv_Chat.setLayoutManager(linearLayoutManager);
//            adapter = new SocialChatAdapter(context, chatModels);
//        }
//        rv_Chat.setAdapter(adapter);
//    }
//
//    private void smothScroll() {
//        rv_Chat.smoothScrollToPosition(chatModels.size());
//    }
//
//    private Bitmap getBitmap(String path) {
//
//        Uri uri = Uri.fromFile(new File(path));
//        InputStream in = null;
//        try {
//            final int IMAGE_MAX_SIZE = 1200000; // 1.2MP
//            in = getContentResolver().openInputStream(uri);
//
//            // Decode image size
//            BitmapFactory.Options o = new BitmapFactory.Options();
//            o.inJustDecodeBounds = true;
//            BitmapFactory.decodeStream(in, null, o);
//            in.close();
//
//
//            int scale = 1;
//            while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) >
//                    IMAGE_MAX_SIZE) {
//                scale++;
//            }
//            Log.d("", "scale = " + scale + ", orig-width: " + o.outWidth + ", orig-height: " + o.outHeight);
//
//            Bitmap b = null;
//            in = getContentResolver().openInputStream(uri);
//            if (scale > 1) {
//                scale--;
//                // scale to max possible inSampleSize that still yields an image
//                // larger than target
//                o = new BitmapFactory.Options();
//                o.inSampleSize = scale;
//                b = BitmapFactory.decodeStream(in, null, o);
//
//                // resize to desired dimensions
//                int height = b.getHeight();
//                int width = b.getWidth();
//                Log.d("", "1th scale operation dimenions - width: " + width + ", height: " + height);
//
//                double y = Math.sqrt(IMAGE_MAX_SIZE
//                        / (((double) width) / height));
//                double x = (y / height) * width;
//
//                Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, (int) x,
//                        (int) y, true);
//                b.recycle();
//                b = scaledBitmap;
//
//                System.gc();
//            } else {
//                b = BitmapFactory.decodeStream(in);
//            }
//            in.close();
//
//            Log.d("", "bitmap size - width: " + b.getWidth() + ", height: " +
//                    b.getHeight());
//            return b;
//        } catch (IOException e) {
//            Log.e("", e.getMessage(), e);
//            return null;
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK) {
//            switch (requestCode) {
//                case CAMERA_CODE:
//                    Uri imageUri = Uri.parse(chatImage.getPath());
//                    Log.e(TAG, "onActivityResult: " + imageUri.getPath());
//                    sendAndGetBinaryData(chatImage.getPath(), 1);
//                    SEND_MEDIA = "media";
//                    type = "image";
//                    sendPath = false;
//                    setPlaceholder(type, chatImage.getPath(), SEND_MEDIA);
//
//                    break;
//
//
//                case GALLERY_CODE:
//                    if (data != null) {
//                        Uri uri = data.getData();
//                        if (uri != null) {
//
//                            String path = getPath(uri);
//                            Log.e(TAG, "onActivityResult:path " + path);
//                            sendAndGetBinaryData(path, 1);
//                            if (path.contains(".mp4") || path.contains(".3gp")) {
//                                SEND_MEDIA = "media";
//                                type = "video";
//                                sendPath = true;
//                                setPlaceholder(type, path, SEND_MEDIA);
//                            } else {
//                                type = "image";
//                                SEND_MEDIA = "media";
//                                sendPath = false;
//                                setPlaceholder(type, path, SEND_MEDIA);
//                            }
//                            Log.e(TAG, "onActivityResult: " + path);
//                        } else {
//                        }
//
//                    }
//                    break;
//                case REQUEST_CODE_DOC:
//                    if (data != null)
//                    {
//                        Uri uri = null;
//                        if (data != null) // data can be null only for image capture
//                            uri = data.getData();
//                        String filePath = null;
//                        try {
//                            filePath = getPath(this, uri);
//                            Log.e("filepath", "file path is" + filePath);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        if (filePath != null) {
//                            chatImage = new File(filePath);//Get pdf file from storage
//
//
//                            if (chatImage != null) {
//
//                                long docSize = chatImage.length();
//                                long mbSize = docSize / 1024;
//                                Log.e("filename", "The file name:" + chatImage.getName());
//
//                                if ((chatImage.getName()).contains(".pdf")) {
//                                    type = ".pdf";
//                                    setPlaceholder(type,chatImage.getPath(),SEND_MEDIA);
//                                    SEND_MEDIA = "media";
//                                    sendAndGetBinaryData(chatImage.getPath(), 1);
//                                } else if (chatImage.getName().contains(".docx") || chatImage.getName().contains(".doc")) {
//                                    type = ".doc";
//                                    setPlaceholder(type,chatImage.getPath(),SEND_MEDIA);
//                                    SEND_MEDIA = "media";
//                                    sendAndGetBinaryData(chatImage.getPath(), 1);
//
//                                } else if (chatImage.getName().contains(".ppt")) {
//                                    type = ".ppt";
//                                    setPlaceholder(type,chatImage.getPath(),SEND_MEDIA);
//                                    SEND_MEDIA = "media";
//                                    sendAndGetBinaryData(chatImage.getPath(), 1);
//
//                                }
//
//
//                            }
//                        }
//
//
//
//
//
//                    }
//
//
//                    break;
//            }
//        }
//    }
//
//    public static String getMimeType(String url) {
//        String type = null;
//        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
//        if (extension != null) {
//            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
//        }
//        return type;
//    }
//
//    private void browseDocuments() {
//
//        String[] mimeTypes =
//                {"application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
//                        "application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
//                        "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
//                        "text/plain",
//                        "application/pdf",
//                        "application/zip"};
//
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
//            if (mimeTypes.length > 0) {
//                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
//            }
//        } else {
//            String mimeTypesStr = "";
//            for (String mimeType : mimeTypes) {
//                mimeTypesStr += mimeType + "|";
//            }
//            intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
//        }
//        startActivityForResult(Intent.createChooser(intent, "ChooseFile"), REQUEST_CODE_DOC);
//
//    }
//
//    private void setPlaceholder(String type, String path, String send_media) {
//        ChatModel model = new ChatModel();
//        model.setMessage("");
//        model.setAttachment(path);
//        model.setSender_id(SharedPreference.retrieveKey(context, CommonKeys.KEY_ID));
//        model.setReceiver_id(receiverId);
//        model.setSendNow("2");
//        model.setAttachment_Type(type);
//        model.setMessage_type(send_media);
//        model.setCheck_status("unsent");
//        chatModels.add(model);
//        adapter.notifyDataSetChanged();
//
//    }
//
//    /*................todo get path for image in gallery............*/
//    private String getPath(Uri uri)
//    {
//
//        if (uri == null) {
//// TODO perform some logging or show user feedback
//            return null;
//        }
//
//        String[] projection = {MediaStore.Images.Media.DATA};
//        Cursor cursor = managedQuery(uri, projection, null, null, null);
//        if (cursor != null) {
//            int column_index = cursor
//                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//            cursor.moveToFirst();
//            Log.e("imggg", cursor.getString(column_index));
//            return cursor.getString(column_index);
//        }
//        return uri.getPath();
//    }
//
//
//    public static String getPath(final Context context, final Uri uri) {
//
//        if (BuildConfig.DEBUG) {
//            Log.e("TAG" + " File -",
//                    "Authority: " + uri.getAuthority() +
//                            ", Fragment: " + uri.getFragment() +
//                            ", Port: " + uri.getPort() +
//                            ", Query: " + uri.getQuery() +
//                            ", Scheme: " + uri.getScheme() +
//                            ", Host: " + uri.getHost() +
//                            ", Segments: " + uri.getPathSegments().toString()
//            );
//
//            final boolean isKitKatPlus = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
//
//            // DocumentProvider
//            if (isKitKatPlus && DocumentsContract.isDocumentUri(context, uri)) {
//                // ExternalStorageProvider
//                Log.e("kitKat", "kitkat methiod is worrking");
//                if (isExternalStorageDocument(uri)) {
//                    Log.e("kitkat", "kitkat if condition is working");
//                    final String docId = DocumentsContract.getDocumentId(uri);
//                    final String[] split = docId.split(":");
//                    final String type = split[0];
//
//                    if ("primary".equalsIgnoreCase(type)) {
//                        return Environment.getExternalStorageDirectory() + "/" + split[1];
//                    } else {
//                        return Environment.getExternalStorageDirectory() + "/" + split[1];
//                    }
//
//                    // TODO handle non-primary volumes
//                }
//                // DownloadsProvider
//                else if (isDownloadsDocument(uri)) {
//                    Log.e("Isdownloaddocument", "isdownload document is working");
//                    final String id = DocumentsContract.getDocumentId(uri);
//                    final Uri contentUri = ContentUris.withAppendedId(
//                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
//                    Log.e("contentUri", "getPath: " + contentUri);
//                    return getDataColumn(context, contentUri, null, null);
//                }
//                // MediaProvider
//                else if (isMediaDocument(uri)) {
//                    Log.e("isMediaDownload", "isMediadownload is working");
//                    final String docId = DocumentsContract.getDocumentId(uri);
//                    final String[] split = docId.split(":");
//                    final String type = split[0];
//
//                    Uri contentUri = null;
//                    if ("image".equals(type)) {
//                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//                    } else if ("video".equals(type)) {
//                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
//                    } else if ("audio".equals(type)) {
//                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//                    }
//
//                    final String selection = "_id=?";
//                    final String[] selectionArgs = new String[]{
//                            split[1]
//                    };
//                    return getDataColumn(context, contentUri, selection, selectionArgs);
//                }
//
//                /*
//                 * check whether the document is selected from google drive
//                 * then get the filename and path from the uri
//                 *
//                 * @Params uri
//                 * */
//                else if (isGoogleDocsUri(uri)) {
//                    Log.e("contentUri", "getPath: " + "GOOGLE DRIVE");
//                    String filename = "";
//                    String filePath = "";
//                    String mimeType = context.getContentResolver().getType(uri);
//                    if (mimeType == null) {
//                        String path = getPath(context, uri);
//                        if (path == null) {
//                            filename = FilenameUtils.getName(uri.toString());
//                        } else {
//                            File file = new File(path);
//                            filename = file.getName();
//                        }
//                    } else {
//                        Cursor returnCursor = context.getContentResolver().
//                                query(uri, null, null, null, null);
//                        int nameIndex = returnCursor.
//                                getColumnIndex(OpenableColumns.DISPLAY_NAME);
//                        returnCursor.moveToFirst();
//                        filename = returnCursor.getString(nameIndex);
//                    }
//                    String path = makeDirectory(context);
//                    File fileSave = new File(path);
//                    filePath = fileSave.getAbsolutePath() + "/" + filename;
//                    Log.e("contentUri", "getPath:filePath " + filePath);
//                    try {
//                        copyFileStream(new File(filePath), uri, context);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    return filePath;
//                }
//            }
//        }
//        // MediaStore (and general)
//        else if ("content".equalsIgnoreCase(uri.getScheme())) {
//            // Return the remote address
//            if (isGooglePhotosUri(uri))
//                return uri.getLastPathSegment();
//            return getDataColumn(context, uri, null, null);
//        }
//        // File
//        else if ("file".equalsIgnoreCase(uri.getScheme())) {
//            return uri.getPath();
//        }
//        return null;
//    }
//
//
//    private static String makeDirectory(Context context) {
//
//        File directory = null;
//        String profile_image_path = "";
//
////        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//        if (Environment.getExternalStorageState().equals(Environment.DIRECTORY_DOWNLOADS)) {
////            directory = new File(Environment.getExternalStorageDirectory() + "/surverybe_drive_files");
//            directory = new File(Environment.
//                    getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//                    + "/Cryptted");
//            if (!directory.exists())
//                directory.mkdirs();
//        } else {
//            directory = context.getDir("Cryptted", context.MODE_PRIVATE);
//            if (!directory.exists())
//                directory.mkdirs();
//        }
//
//        if (directory != null) {
//            File profile_image = new File(directory + File.separator + "Cryptted");
//            if (!profile_image.exists())
//                profile_image.mkdirs();
//
//            profile_image_path = directory + File.separator + "Cryptted";
//
//        }
//        return profile_image_path;
//    }
//
//    private static void copyFileStream(File dest, Uri uri, Context context)
//            throws IOException {
//        InputStream is = null;
//        OutputStream os = null;
//        try {
//            is = context.getContentResolver().openInputStream(uri);
//            os = new FileOutputStream(dest);
//            byte[] buffer = new byte[1024];
//            int length;
//
//            while ((length = is.read(buffer)) > 0) {
//                os.write(buffer, 0, length);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            is.close();
//            os.close();
//        }
//    }
//
//    public static boolean isExternalStorageDocument(Uri uri) {
//        return "com.android.externalstorage.documents".equals(uri.getAuthority());
//    }
//
//    public static boolean isDownloadsDocument(Uri uri) {
//        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
//    }
//
//    public static boolean isGoogleDocsUri(Uri uri) {
//        return "com.google.android.apps.docs.storage".equals(uri.getAuthority());
//    }
//
//    public static boolean isGooglePhotosUri(Uri uri) {
//        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
//    }
//
//    public static boolean isMediaDocument(Uri uri) {
//        return "com.android.providers.media.documents".equals(uri.getAuthority());
//    }
//
//    public static String getDataColumn(Context context, Uri uri, String selection,
//                                       String[] selectionArgs) {
//
//        Cursor cursor = null;
//        final String column = "_data";
//        final String[] projection = {
//                column
//        };
//        try {
//            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
//                    null);
//            if (cursor != null && cursor.moveToFirst()) {
//                DatabaseUtils.dumpCursor(cursor);
//                final int column_index = cursor.getColumnIndexOrThrow(column);
//                return cursor.getString(column_index);
//            }
//        } finally {
//            if (cursor != null)
//                cursor.close();
//        }
//        return null;
//    }
//
//    private void sendAndGetBinaryData(String imgPath, int i) {
//        String uni_code = String.valueOf(System.currentTimeMillis());
//        media_path.put(uni_code, imgPath);
//
//        if (media_path.size() == 1) {
//            uploadFileOnServer(media_path);
//        }
//
//    }
//
//
//    @SuppressLint("ObsoleteSdkInt")
//    private void uploadFileOnServer(HashMap<String, String> map) {
//        Log.e("ChatClass", "uploadFileOnServer: " + map.keySet());
//        if (map.size() > 0) {
//            for (String entry : map.keySet()) {
//                String key = entry;
//                String value = map.get(key);
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
//                    new FileUploadTask(value, key).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
//                break;
//            }
//        }
//    }
//
//    @SuppressLint("StaticFieldLeak")
//    private class FileUploadTask extends AsyncTask<String, Integer, String> {
//        private String file_path = "";
//        //   private String receiver_id = "";
//        private String attachment_type = "";
//        private String uni_code = "";
//
//        private UploadFileMoreDataReqListener callback;
//        private FileUploadManager mFileUploadManager;
//
//        public FileUploadTask(String file_path, String uni_code) {
//            this.file_path = file_path;
//            // receiver_id = "0";
//            this.uni_code = uni_code;
//            this.attachment_type = SEND_MEDIA;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            mFileUploadManager = new FileUploadManager();
//        }
//
//        @Override
//        protected String doInBackground(String... params) {
//            boolean isSuccess = mSocket.connected();
//
//            Log.e("isSuccesssss", String.valueOf(isSuccess));
//
//            if (isSuccess) {
//                mFileUploadManager.prepare(file_path);
//
//                Log.e("file_path", "" + file_path);
//                // This function gets callback when server requests more data
//
//                // Tell server we are ready to start uploading ..
//                if (mSocket.connected()) {
//                    JSONArray jsonArr = new JSONArray();
//                    JSONObject res = new JSONObject();
//                    try {
//                        res.put("Name", mFileUploadManager.getFileName());
//                        res.put("Size", mFileUploadManager.getFileSize());
//                        res.put("room_id", roomId);
//                        jsonArr.put(res);
//                        Log.e("jsonArr", "" + jsonArr.toString());
//                        mSocket.emit("uploadFileStart", jsonArr);
//                    } catch (JSONException e) {
//                        //TODO: Log errors some where..
//                    }
//                }
//                setUploadFileMoreDataReqListener(mUploadFileMoreDataReqListener);
//                // This function will get a call back when upload completes
//                setUploadFileCompleteListener();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            if (s == null) {
//                return;
//            }
//            if (s.equalsIgnoreCase("OK")) {
//                Log.e("SocialChat", "onPostExecute: " + "aaaaaaaaaa");
//                media_path.remove(uni_code);
//                mFileUploadManager.close();
//                mSocket.off("uploadFileMoreDataReq", uploadFileMoreDataReq);
//                mSocket.off("uploadFileCompleteRes", onCompletedddd);
//                uploadFileOnServer(media_path);
//            }
//        }
//
//        @Override
//        protected void onProgressUpdate(Integer... values) {
//            // super.onProgressUpdate(values);
//            if (values[0] > 107) {
//                if (media_path.containsKey(uni_code)) {
//                    onPostExecute("OK");
//                }
//            }
//        }
//
//        private UploadFileMoreDataReqListener mUploadFileMoreDataReqListener = new UploadFileMoreDataReqListener() {
//            @Override
//            public void uploadChunck(int offset, int percent) {
//                // Read the next chunk
//                mFileUploadManager.read(offset);
//                if (mSocket.connected()) {
//                    JSONArray jsonArr = new JSONArray();
//                    JSONObject res = new JSONObject();
//                    /*try {
//                        res.put("Name", mFileUploadManager.getFileName());
//                        res.put("chunkSize", mFileUploadManager.getBytesRead());
//                        res.put("room_id", roomId);
//                        res.put("sender_id",
//                                SharedPreference.retrieveKey(context, CommonKeys.KEY_ID));
//                        res.put("receiver_id", receiverId);
//                        res.put("message", "Read Attachment");
//                        res.put("unique_code", uni_code);
//                        if (sendPath){
//                            res.put("filePath", file_path);
//                        }
//                        res.put("flag", "new");
//                        res.put("check_status", "send");
//                        res.put("message_type", SEND_MEDIA);
//                        res.put("attachment_type", type);
//                        res.put("Data", mFileUploadManager.getData());
//                        jsonArr.put(res);
//                        mSocket.emit("uploadFileChuncks", jsonArr);
//                        Log.e("ChatClass", "uploadChunck:Array " + jsonArr.toString());
//
//                    } catch (JSONException e) {
//                        e.getMessage();
//                        //TODO: Log errors some where..
//                    }*/
//                    try {
//                        res.put("Name", mFileUploadManager.getFileName());
//                        res.put("Size", mFileUploadManager.getBytesRead());
//                        res.put("room_id", roomId);
//                        //res.put("flag", "new");
//                        res.put("sender_id",
//                                SharedPreference.retrieveKey(context, CommonKeys.KEY_ID));
//                        res.put("receiver_id", receiverId);
//                        res.put("message", file_path);
//                        res.put("unique_code", uni_code);
//                        res.put("attachment_type", type);
//                        res.put("message_type", SEND_MEDIA);
//                        res.put("Data", mFileUploadManager.getData());
//                        jsonArr.put(res);
//                        mSocket.emit("uploadFileChuncks", jsonArr);
//                        Log.e("ChatClass", "uploadChunck:Array " + jsonArr.toString());
//
//                    } catch (JSONException e) {
//                        e.getMessage();
//                        //TODO: Log errors some where..
//                    }
//                }
//            }
//
//            @Override
//            public void err(JSONException e) {
//                // TODO Auto-generated method stub
//            }
//        };
//
//        Emitter.Listener uploadFileMoreDataReq = new Emitter.Listener() {
//            @Override
//            public void call(Object... args) {
//                Log.e("ChatClass", "call: " + "uploadFileMoreDataReq");
//                for (int jj = 0; jj < args.length; jj++) {
//                    Log.e("eDataReqListener", "setUploadFileMoreDataReqListener-- " + args[jj]);
//                }
//
//                try {
//                    JSONObject json_data = (JSONObject) args[0];
//                    int place = json_data.getInt("Place");
//                    int percent = json_data.getInt("Percent");
//                    publishProgress(json_data.getInt("Percent"));
//
//                    callback.uploadChunck(place, percent);
//
//                } catch (JSONException e) {
//                    callback.err(e);
//                }
//            }
//        };
//
//
//        Emitter.Listener onCompletedddd = new Emitter.Listener() {
//            @Override
//            public void call(Object... args) {
//                JSONObject json_data = (JSONObject) args[0];
//                Log.e(TAG, "call:json_data " + json_data);
//                if (json_data.has("IsSuccess")) {
//                    publishProgress(110);
//                    return;
//                }
//            }
//        };
//
//        private void setUploadFileMoreDataReqListener(final UploadFileMoreDataReqListener callbackk) {
//            Log.e("ChatClass", "setUploadFileMoreDataReqListener: ");
//            callback = callbackk;
//            mSocket.on("uploadFileMoreDataReq", uploadFileMoreDataReq);
//        }
//
//        private void setUploadFileCompleteListener() {
//            mSocket.on("uploadFileCompleteRes", onCompletedddd);
//        }
//    }
//
//}
