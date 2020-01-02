package client2.game.lucky.luckygameclient2.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.rengwuxian.materialedittext.MaterialEditText;

import client2.game.lucky.luckygameclient2.R;
import io.socket.client.Socket;

public class MainActivity extends AppCompatActivity {

    // objects
    // TextView
    TextView txt_count, txt_result, txt_money, txt_status, txt_score;
    // Buttons
    Button button_submit, button_disconnect;
    // MaterialEditText
    MaterialEditText edit_place_money, edit_place_value;

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
                    // socket.disconnect();
                    button_disconnect.setText(R.string.button_value_connect);
                }
                else
                {
                    // socket.connect();
                    button_disconnect.setText(R.string.button_value_disconnect);
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
                    if(!canPlay) // DEBUG or true
                    {
                        // BET / Sazka
                        if(!isBet)
                        {
                            // continue
                        }
                        else
                        {
                            StyleableToast.makeText(MainActivity.this, getString(R.string.lucky_game_message_you_already_turn), R.style.toast_lucky_game_blue_cube).show();
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
                    StyleableToast.makeText(v.getContext(), getString(R.string.exception_socket_client_message) + exc.getMessage(), R.style.toast_exception_error).show();
                }
            }
        });

    }
}































