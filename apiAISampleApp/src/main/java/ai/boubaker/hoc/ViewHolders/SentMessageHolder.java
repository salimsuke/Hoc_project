package ai.boubaker.hoc.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.phoeniciait.hocassistant.R;

import ai.boubaker.hoc.Models.UserMessage;
import ai.boubaker.hoc.Utils.Helpers;

/**
 * Created by Salim on 5/15/2018.
 */

public class SentMessageHolder extends RecyclerView.ViewHolder {

    TextView messageText, timeText;

    public SentMessageHolder(View itemView) {
        super(itemView);
        messageText = (TextView) itemView.findViewById(R.id.text_message_body);
        timeText = (TextView) itemView.findViewById(R.id.text_message_time);
    }

    public void bind(UserMessage message) {
        messageText.setText(message.getMessage());

        // Format the stored timestamp into a readable String using method.
        timeText.setText(Helpers.getDate(message.getCreatedAt(),5000));

    }
}