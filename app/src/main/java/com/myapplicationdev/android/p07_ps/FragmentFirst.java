package com.myapplicationdev.android.p07_ps;


import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentFirst extends Fragment {

    EditText etNum;
    Button btnRetrieveNum;
    TextView tvNum;

    public FragmentFirst() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_first, container, false);
        etNum = view.findViewById(R.id.etContainNum);
        btnRetrieveNum = view.findViewById(R.id.btnRetrieveNum);
        tvNum = view.findViewById(R.id.tvNumSMS);

        btnRetrieveNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = etNum.getText().toString();
                if (!TextUtils.isEmpty(data)) {
                    Uri uri = Uri.parse("content://sms");
                    String[] reqCols = new String[]{"date", "address", "body", "type"};
                    String filter = "address LIKE ?";
                    String[] args = {"%" + data + "%"};
                    ContentResolver cr = getActivity().getContentResolver();
                    Cursor cursor = cr.query(uri, reqCols, filter, args, null);
                    String smsBody = "";
                    if (cursor.moveToFirst()) {
                        do {
                            long dateInMillis = cursor.getLong(0);
                            String date = (String) DateFormat.format("dd MM yyyy h:mm:ss aa", dateInMillis);
                            String address = cursor.getString(1);
                            String body = cursor.getString(2);
                            String type = cursor.getString(3);
                            if (type.equalsIgnoreCase("1")) {
                                type = "Inbox: ";
                            } else {
                                type = "Sent: ";
                            }
                            smsBody += type + " " + address + "\n at " + date
                                    + "\n\"" + body + "\"\n\n";
                        } while (cursor.moveToNext());
                    }
                    tvNum.setText(smsBody);
                    if (smsBody.isEmpty()) {
                        Toast.makeText(getActivity(), "No Results Found", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getActivity(), "Please Enter A Number", Toast.LENGTH_SHORT).show();
                }

            }
        });
        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 0: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    btnRetrieveNum.performClick();
                } else {
                    Toast.makeText(getContext(), "Permission not granted", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}
