package ai.boubaker.hoc.ViewHolders;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.phoeniciait.hocassistant.R;

import ai.boubaker.hoc.AIApplication;
import ai.boubaker.hoc.Models.UserMessage;
import ai.boubaker.hoc.Utils.Helpers;

/**
 * Created by Salim on 5/15/2018.
 */

public class ReceivedMessageHolder extends RecyclerView.ViewHolder {

    TextView messageText, timeText, nameText;
    ImageView profileImage;

    public ReceivedMessageHolder(View itemView) {
        super(itemView);
        messageText = (TextView) itemView.findViewById(R.id.text_message_body);
        timeText = (TextView) itemView.findViewById(R.id.text_message_time);
        nameText = (TextView) itemView.findViewById(R.id.text_message_name);
        profileImage = (ImageView) itemView.findViewById(R.id.image_message_profile);
    }

    public void bind(final UserMessage message) {
        messageText.setText(message.getMessage());

        // Format the stored timestamp into a readable String using method.
        timeText.setText(Helpers.getDate(message.getCreatedAt(),0));
        nameText.setText(message.getSender().getNickname());

        // Insert the profile image from the URL into the ImageView.
        //Utils.displayRoundImageFromUrl(mContext, message.getSender().getProfileUrl(), profileImage);
        profileImage.setImageResource(R.drawable.ic_chat_bot);

        messageText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (message.getMessage().contains("For more information press here.")){
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(AIApplication.more_infos_uri));
                    AIApplication.appInstance.startActivity(intent);
                }
            }
        });

    }
}