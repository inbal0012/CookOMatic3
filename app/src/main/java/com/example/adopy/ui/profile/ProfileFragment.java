package com.example.adopy.ui.profile;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.adopy.Activities.EditProfileActivity;
import com.example.adopy.R;
import com.example.adopy.Utilities.Interfaces_and_Emuns.Gender;
import com.example.adopy.Utilities.Models.User;
import com.example.adopy.Utilities.MyImage;
import com.example.adopy.Utilities.MyLocation;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import static com.example.adopy.Utilities.RequestCodes.USER_IMAGE_REQUEST;

public class ProfileFragment extends Fragment {

    private static final String TAG = "my_ProfileFragment";
    private ProfileViewModel profileViewModel;

    //firebase
    private FirebaseUser fuser;
    private StorageReference storageReference;
    private StorageTask uploadTask;

    private User user;

    private View root;
    private Toolbar toolbar;
    private String nameStr;

    private Handler mainHandler;

    private Uri imageUri;
    MyImage myImage;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_profile, container, false);

        nameStr = "hello";
        toolbar = root.findViewById(R.id.toolbar);
        toolbar.setTitle(nameStr);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mainHandler = new Handler(getContext().getMainLooper());
        Log.d(TAG, "onCreateView: " + ((AppCompatActivity) getActivity()).getSupportActionBar());

        //storage
        storageReference = FirebaseStorage.getInstance().getReference("upload");

        FloatingActionButton fab = (FloatingActionButton) root.findViewById(R.id.fabEdit);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), EditProfileActivity.class));
            }
        });

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (fuser != null) {
            populateData();
//            Log.d(TAG, "onCreateView: " + user.getUsername());
//            nameStr = user.getUsername();
            toolbar = root.findViewById(R.id.toolbar);
            toolbar.setTitle("test");
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("test");
            Log.d(TAG, "onIf: " + ((AppCompatActivity) getActivity()).getSupportActionBar());
            updateToolbar();

        }

        mainHandler = new Handler(getContext().getMainLooper());
        return root;
    }

    private void updateToolbar() {
        Log.d(TAG, "updateToolbar: ");
        if (user == null) {
            Log.d(TAG, "updateToolbar: user null");
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    Log.d(TAG, "updateToolbar: run: ");
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        Log.d(TAG, "updateToolbar: run: error " + e.getMessage());
                        e.printStackTrace();
                    }
                    Log.d(TAG, "updateToolbar: run: after sleep");
                    updateToolbar();
                }
            }.start();
        } else {
            Log.d(TAG, "updateToolbar: else");

            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "updateToolbar: handler " + ((AppCompatActivity) getActivity()).getSupportActionBar());
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(user.getUsername());
                }
            });
        }

        Log.d(TAG, "updateToolbar: END");
    }

    private void populateData() {
        DatabaseReference mReference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                user = dataSnapshot.getValue(User.class);

                //user name
                //getActivity().setTitle(user.getUsername());   //TODO
                Log.d(TAG, "onDataChange: " + ((AppCompatActivity) getActivity()).getSupportActionBar());
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("boo");

                //email
                TextView userEmailTv = root.findViewById(R.id.tvEmail);
                String userEmail = fuser.getEmail();
                userEmailTv.setText(userEmail);

                //image
                ImageView profile_image = root.findViewById(R.id.profile_image);
                profile_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myImage = new MyImage(getActivity(), "Users", "user");
                        myImage.openImage();
                        //openImage();
                    }
                });
                if (user.getImageUri().equals("default")) {
                    profile_image.setImageResource(R.drawable.user_male);
                    if (user.getGender().equals("Female")) {
                        profile_image.setImageResource(R.drawable.user_female);
                    }
                } else {
                    Glide.with(getActivity()).load(user.getImageUri()).into(profile_image);
                }

                //gender
                ImageView ivGender = root.findViewById(R.id.ivGender);
                Drawable myDrawable;
                if (user.getGender().equals(Gender.Male)) {
                    myDrawable = getResources().getDrawable(R.drawable.ic_gender_male);
                } else {
                    myDrawable = getResources().getDrawable(R.drawable.ic_gender_female);
                }
                ivGender.setImageDrawable(myDrawable);

                //age
                TextView tvAge = root.findViewById(R.id.tvUserAge);
                tvAge.setText(user.getAge());

                //location
                MyLocation myLocation = new MyLocation(getActivity());
                Address address = myLocation.getAddress();
                TextView tvLocation = root.findViewById(R.id.tvLocation);
                tvLocation.setText(myLocation.StringFromAddress(address));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, USER_IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage(getString(R.string.uploading));
        pd.show();

        if (imageUri != null) {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    +"."+getFileExtension(imageUri));

            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("imageUri", mUri);
                        reference.updateChildren(map);

                        pd.dismiss();
                    } else {
                        Toast.makeText(getContext(), getString(R.string.failed), Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        } else {
            Toast.makeText(getContext(), getString(R.string.no_image_selected), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult: " + requestCode);
        if (requestCode == USER_IMAGE_REQUEST) {
            myImage.onActivityResult(requestCode, resultCode, data);
//
//            imageUri = data.getData();
//
//            if (uploadTask != null && uploadTask.isInProgress()) {
//                Toast.makeText(getContext(), getString(R.string.upload_in_progress), Toast.LENGTH_SHORT).show();
//            } else {
//                uploadImage();
//            }
        }
    }
}
