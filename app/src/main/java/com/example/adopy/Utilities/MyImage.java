package com.example.adopy.Utilities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.example.adopy.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.example.adopy.Utilities.RequestCodes.PET_IMAGE_REQUEST;
import static com.example.adopy.Utilities.RequestCodes.USER_IMAGE_REQUEST;

public class MyImage {
    private Activity activity;

    //firebase
    private FirebaseUser fuser;
    private StorageReference storageReference;
    private StorageTask uploadTask;

    private Uri imageUri;
    private String path;
    private String key;
    //TODO

    public MyImage(Activity activity, String path, String key) {
        this.activity = activity;
        this.path = path;
        this.key = key;
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("upload");
    }

    public void openImage() {
        int request = 0;
        if (path.equals("Users")) {
            request = USER_IMAGE_REQUEST;
        }
        else {
            request = PET_IMAGE_REQUEST;
        }
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(intent, request);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = activity.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {
        final ProgressDialog pd = new ProgressDialog(activity);
        pd.setMessage(activity.getString(R.string.uploading));
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

                        DatabaseReference reference;
                        if (path.equals("Users")) {
                            reference= FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
                        }
                        else {
                            reference= FirebaseDatabase.getInstance().getReference("Pets").child(key);
                        }
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("imageUri", mUri);
                        reference.updateChildren(map);

                        pd.dismiss();
                    } else {
                        Toast.makeText(activity, activity.getString(R.string.failed), Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        } else {
            Toast.makeText(activity, activity.getString(R.string.no_image_selected), Toast.LENGTH_SHORT).show();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == USER_IMAGE_REQUEST || requestCode == PET_IMAGE_REQUEST && data != null && data.getData() != null) {
            imageUri = data.getData();

            if (uploadTask != null && uploadTask.isInProgress()) {
                Toast.makeText(activity, activity.getString(R.string.upload_in_progress), Toast.LENGTH_SHORT).show();
            } else {
                uploadImage();
            }
        }
    }
}
