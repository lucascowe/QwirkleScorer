package keepitsimple.store.qwirklescorer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    int turnScore;
    String moveString = "";
    TextView txvCurrentMove;

    public void onClick (View view) {
        Button button = (Button) view;
        Log.i("Debug","Button pressed");
        int num = Integer.parseInt(button.getTag().toString());
        switch (num) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                turnScore += num;
                // if not first move, add +
                if (moveString.length() > 0) {
                    moveString += " + " + String.valueOf(num);
                } else {
                    moveString = String.valueOf(num);
                }
                Log.i("Debug","move so far is " + moveString);
                txvCurrentMove.setText(moveString);
                break;
            case 6:
                Log.i("Debug","Q" + moveString);

                turnScore += 12;
                // if not first move, add +
                if (moveString.length() > 0) {
                    moveString += " + QWIRKLE";
                } else {
                    moveString = "QWIRKLE";
                }
                txvCurrentMove.setText(moveString);
                break;
            // save
            case 7:
                break;
            // clear
            case 8:
                moveString = "";
                turnScore = 0;
                break;
            // delete
            case 9:
                String[] turns = moveString.split(" ",12);
                moveString = "";
                turnScore = 0;
                for (int i = 0; i < turns.length - 1; i+= 2) {
                    Log.i("Debug","this string is:  " + turns[i]);
                    if (i == 0) {
                        moveString = String.format("%s",turns[i]);
                    } else {
                        moveString = String.format("%s + %s", moveString, turns[i]);
                    }
                    if (turns[i].equals("QWIRLE")) {
                        turnScore += 12;
                    } else {
                        turnScore += Integer.parseInt(turns[i]);
                    }
                }
                break;
            default:
                Log.i("Error","Invalid button tag");

        }
        txvCurrentMove.setText(moveString);
        Log.i("Debug","Score is " + turnScore);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txvCurrentMove = findViewById(R.id.textView);
        txvCurrentMove.setText("");
    }
}

