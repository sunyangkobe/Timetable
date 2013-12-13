package edu.cmu.cs.vlis.timetable.adapter;

import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import edu.cmu.cs.vlis.timetable.R;
import edu.cmu.cs.vlis.timetable.listener.FriendsItemClickListener;
import edu.cmu.cs.vlis.timetable.obj.Friend;
import edu.cmu.cs.vlis.timetable.util.Utils;

public class FriendsArrayAdapter extends ArrayAdapter<Friend> {
    private Fragment currentFragment;

    public FriendsArrayAdapter(Fragment currentFragment, Friend[] friends) {
        super(currentFragment.getActivity(), R.layout.friend_item, friends);
        this.currentFragment = currentFragment;
    }

    public static class ViewHolder {
        public ImageView profileImage;
        public TextView fullName;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        ViewHolder viewHolder;

        if (rowView == null) {
            LayoutInflater inflater = currentFragment.getActivity().getLayoutInflater();
            rowView = inflater.inflate(R.layout.friend_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.profileImage = (ImageView) rowView.findViewById(R.id.friendProfileImage);
            viewHolder.fullName = (TextView) rowView.findViewById(R.id.fullName);

            rowView.setTag(viewHolder);
        }

        viewHolder = (ViewHolder) rowView.getTag();
        Friend friend = getItem(position);

        Bitmap profileImageBitmap = Utils.convertBase64StringToBitmap(friend.getAvatar());
        viewHolder.profileImage.setImageBitmap(profileImageBitmap);

        viewHolder.fullName.setText(friend.getFullname());

        rowView.setOnClickListener(new FriendsItemClickListener(currentFragment.getActivity(),
                friend.getId()));

        return rowView;
    }

}
