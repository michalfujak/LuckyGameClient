package client2.game.lucky.luckygameclient2.Activity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONObject;

import client2.game.lucky.luckygameclient2.Common.Common;
import client2.game.lucky.luckygameclient2.R;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.client.SocketIOException;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity {

    // objects
    // TextView
    TextView txt_count, txt_result, txt_money, txt_status, txt_score;
    // Buttons
    Button button_submit, button_disconnect;
    // MaterialEditText
    MaterialEditText edit_place_money, edit_place_value;

    // AlertDialog
    Dialog youAlreadyTurnDialog;
    // Buttons
    Button youAlreadyTurnButtonContinue;
    //
    ImageView youAlreadyTurnImageViewClose;

    // Socket.client (io.socket.client)
    Socket socket;

    boolean isDisconnect = false;
    boolean isBet = false;
    boolean canPlay = true;

    int resultNumber = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // StyleableToast.makeText(this, "Start application!", R.style.toast_normal_info_purple).show();
        //
        edit_place_money = (MaterialEditText)findViewById(R.id.money_value_style_edit_text);
        edit_place_value = (MaterialEditText)findViewById(R.id.bet_value_style_edit_text);
        //
        txt_count = (TextView)findViewById(R.id.textCount);
        txt_result = (TextView)findViewById(R.id.textResult);
        txt_money = (TextView)findViewById(R.id.textMoney);
        txt_status = (TextView)findViewById(R.id.textHomeStatus);
        txt_score = (TextView)findViewById(R.id.scoreTextViewDisplay);
        //
        // AlertDialog you already turn initializable
        youAlreadyTurnDialog = new Dialog(this);
        //
        button_submit = (Button)findViewById(R.id.button_submit_action);
        button_disconnect = (Button)findViewById(R.id.button_disconnect);
        //
        // Clicked disconnect
        button_disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                if(!isDisconnect)
                {
                    socket.disconnect();
                    button_disconnect.setText(R.string.button_value_connect);
                    // console information
                    txt_status.setText(R.string.console_message_lucky_game_disconnect);
                }
                else
                {
                    socket.connect();
                    button_disconnect.setText(R.string.button_value_disconnect);
                    // console information
                    txt_status.setText(R.string.console_mesaage_lucky_game_connect);
                }
                isDisconnect = !isDisconnect;
            }
        });
        //
        // Clicked submit
        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                try
                {
                    // socket.connect();
                    if(canPlay) // DEBUG or true
                    {
                        // BET / Sazka
                        if(!isBet)
                        {
                            int money_bet_value = Integer.parseInt(edit_place_money.getText().toString());
                            if(Common.score >= money_bet_value)
                            {
                                //
                                // CREATE JSON Object send
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("money", Integer.parseInt(edit_place_money.getText().toString()));
                                jsonObject.put("betValue", Integer.parseInt(edit_place_value.getText().toString()));
                                //
                                socket.emit("client_send_money", jsonObject);
                                //
                                Common.score -= money_bet_value;
                                // SK -> Priradenie do pola score
                                // add score
                                txt_score.setText(String.valueOf(Common.score));
                                //
                                // Anti multiple bet in 1 turn
                                // SK -> Jeden nasobok pre jedno otocenie.
                                isBet = true;
                            }
                            else
                            {
                                // You not enought money, please restart game!
                                StyleableToast.makeText(MainActivity.this, getString(R.string.lucky_game_message_enought_money_restart_game) , R.style.toast_lucky_game_blue_cube).show();
                            }
                        }
                        else
                        {
                            // You already turn
                            //
                            // StyleableToast.makeText(MainActivity.this, getString(R.string.lucky_game_message_you_already_turn), R.style.toast_lucky_game_blue_cube).show();
                            // V7.AlertDialog
                            youAlreadyTurnDialog.setContentView(R.layout.you_already_turn_alert_dialog);
                            youAlreadyTurnImageViewClose = (ImageView) youAlreadyTurnDialog.findViewById(R.id.you_already_turn_image_view_close);
                            youAlreadyTurnButtonContinue = (Button) youAlreadyTurnDialog.findViewById(R.id.you_already_turn_button_continue);

                            // ImageView clicked
                            youAlreadyTurnImageViewClose.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    youAlreadyTurnDialog.dismiss();
                                }
                            });

                            // Button continue clicked
                            youAlreadyTurnButtonContinue.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    youAlreadyTurnDialog.dismiss();
                                }
                            });
                            // Dialog staring is visibility
                            youAlreadyTurnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            youAlreadyTurnDialog.show();
                        }
                    }
                    else
                    {
                        // Play, to next turn
                        StyleableToast.makeText(MainActivity.this, getString(R.string.lucky_game_message_next_turn), R.style.toast_lucky_game_blue_cube).show();
                    }
                }
                catch(Exception exc)
                {
                    // Exception error Socket.Client error
                    StyleableToast.makeText(v.getContext(), getString(R.string.exception_socket_client_message) + exc.getMessage(), R.style.toast_exception_error).show();
                }
            }
        });

        //
        // Connect socket
        try
        {
            socket = IO.socket(getText(R.string.config_connect_ip).toString());
            //
            // SK -> Udalost a procesne vlakno ktore sa pripoji k serveru prostrednictvom JSONObject
            //
            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            StyleableToast.makeText(MainActivity.this, getString(R.string.message_connected_done), R.style.toast_green_done).show();
                        }
                    });
                }
            });
            // connect
            socket.connect();
        }
        catch(Exception exp)
        {
            // Error connect
            StyleableToast.makeText(MainActivity.this, getString(R.string.exception_socket_connect_message) + exp.getMessage(), R.style.toast_exception_error).show();
        }

        // call method
        registerAllEventForGame();

    }

    /*
    * @function registerAllEventForGame
    * @return null
    *
     */
    private void registerAllEventForGame()
    {
        // register game
        // call JS function broadcast
        socket.on("broadcast", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                // Retriver time
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txt_count.setText(new StringBuilder(getText(R.string.message_timer_txt)).append(args[0]));
                        txt_result.setText("");
                        txt_status.setText("");
                        // continue
                    }
                });
            }
        });
    }
}































