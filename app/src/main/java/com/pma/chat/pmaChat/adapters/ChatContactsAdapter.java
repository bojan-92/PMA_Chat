package com.pma.chat.pmaChat.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pma.chat.pmaChat.data.ChatContactContract;
import com.pma.chat.pmaChat.model.ChatContact;
import com.pma.chat.pmaChat.model.User;
import com.pma.chat.pmaChat.R;
import com.pma.chat.pmaChat.sync.MyFirebaseService;
import com.pma.chat.pmaChat.utils.Helpers;
import com.pma.chat.pmaChat.utils.RemoteConfig;


public class ChatContactsAdapter extends RecyclerView.Adapter<ChatContactsAdapter.ChatContactsAdapterViewHolder> {

    /* The context we use to utility methods, app resources and layout inflaters */
    private final Context mContext;

    /*
     * Below, we've defined an interface to handle clicks on items within this Adapter. In the
     * constructor of our ForecastAdapter, we receive an instance of a class that has implemented
     * said interface. We store that instance in this variable to call the onClick method whenever
     * an item is clicked in the list.
     */
    private final ChatContactsAdapterOnClickHandler mClickHandler;

    /**
     * The interface that receives onClick messages.
     */
    public interface ChatContactsAdapterOnClickHandler {
        void onClick(ChatContact chatContact);
    }

    private Cursor mCursor;

    /**
     * Creates a ChatContactsAdapter.
     *
     * @param context      Used to talk to the UI and app resources
     * @param clickHandler The on-click handler for this adapter. This single handler is called
     *                     when an item is clicked.
     */
    public ChatContactsAdapter(Context context, ChatContactsAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;

    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  If your RecyclerView has more than one type of item (like ours does) you
     *                  can use this viewType integer to provide a different layout. See
     *                  {@link android.support.v7.widget.RecyclerView.Adapter#getItemViewType(int)}
     *                  for more details.
     * @return A new ForecastAdapterViewHolder that holds the View for each list item
     */
    @Override
    public ChatContactsAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

//        Context context = viewGroup.getContext();
//        int layoutId = R.layout.item_chat_contact;
//        LayoutInflater layoutInflater = LayoutInflater.from(context);
//        boolean shouldAttachToParentImmediately = false;
//
//        View view = layoutInflater.inflate(layoutId, viewGroup, shouldAttachToParentImmediately);
//        view.setFocusable(true);
//
//        return new ChatContactsAdapterViewHolder(view);

        int layoutId = R.layout.item_chat_contact;

        View view = LayoutInflater.from(mContext).inflate(layoutId, viewGroup, false);

        view.setFocusable(true);

        return new ChatContactsAdapterViewHolder(view);
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the weather
     * details for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param viewHolder The ViewHolder which should be updated to represent the
     *                                  contents of the item at the given position in the data set.
     * @param position                  The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(ChatContactsAdapterViewHolder viewHolder, int position) {
        mCursor.moveToPosition(position);
        ChatContact chatContact = Helpers.getChatContactFromCursor(mCursor);
        viewHolder.bind(chatContact);
    }

    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available in our contact list
     */
    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    /**
     * A ViewHolder is a required part of the pattern for RecyclerViews. It mostly behaves as
     * a cache of the child views for a chat contact item. It's also a convenient place to set an
     * OnClickListener, since it has access to the adapter and the views.
     */
    class ChatContactsAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView profilePhotoImageView;
        final TextView nameTextView;
        final TextView firebaseNameTextView;
        final TextView statusTextView;

        ChatContactsAdapterViewHolder(View itemView) {
            super(itemView);

            profilePhotoImageView = (ImageView) itemView.findViewById(R.id.iv_profile_photo);
            nameTextView = (TextView) itemView.findViewById(R.id.tv_name);
            firebaseNameTextView = (TextView) itemView.findViewById(R.id.tv_firebase_name);
            statusTextView = (TextView) itemView.findViewById(R.id.tv_status);

            itemView.setOnClickListener(this);
        }

        void bind(ChatContact chatContact) {
            nameTextView.setText(chatContact.getName());
            firebaseNameTextView.setText("(" + chatContact.getFirebaseName() + ")");
            DatabaseReference chatContactRef = MyFirebaseService.getUserDatabaseReferenceById(chatContact.getFirebaseUserId());
            chatContactRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    User userInfo = dataSnapshot.getValue(User.class);

                    if(userInfo == null) return;

                    if(userInfo.getFcmToken() != null) {
                        statusTextView.setText("Online");
                        statusTextView.setTextColor(Color.parseColor("#009933"));
                    } else {
                        statusTextView.setText("Offline");
                        statusTextView.setTextColor(Color.parseColor("#808080"));
                    }

                    if(userInfo.getProfileImageUri() != null) {
                        Glide.with(profilePhotoImageView.getContext())
                                .load(userInfo.getProfileImageUri())
                                .apply(RequestOptions.circleCropTransform())
                                .into(profilePhotoImageView);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

        }

        /**
         * This gets called by the child views during a click. We fetch the date that has been
         * selected, and then call the onClick handler registered with this adapter, passing that
         * date.
         *
         * @param v the View that was clicked
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            ChatContact chatContact = Helpers.getChatContactFromCursor(mCursor);
            mClickHandler.onClick(chatContact);
        }
    }
}
