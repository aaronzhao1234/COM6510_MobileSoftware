package uk.ac.shef.oak.com4510.activities;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import uk.ac.shef.oak.com4510.R;
import uk.ac.shef.oak.com4510.model.PathPhoto;

/**
 * This fragment initializes the details view with information
 * from the photo and displays the photo location on the map.
 */
public class PhotoDetailsFragment extends Fragment {

    private PathPhoto pathPhoto;

    public PhotoDetailsFragment(PathPhoto pathPhoto) {
        this.pathPhoto = pathPhoto;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // initialize path map fragment
        FragmentManager fragmentManager = getChildFragmentManager();
        PathMapFragment fragment = new PathMapFragment();

        // set fragment arguments
        Bundle args = new Bundle();
        args.putInt("pathId", pathPhoto.getPathId());
        args.putInt("photoId", pathPhoto.getId());
        fragment.setArguments(args);

        // display path map fragment
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fade_in_short, R.anim.fade_out_short)
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.photo_details_item, container, false);
        ImageView imageView = v.findViewById(R.id.imageView_id);
        TextView textView = v.findViewById(R.id.temperature);
        TextView textView2 = v.findViewById(R.id.pressure);
        TextView textView3 = v.findViewById(R.id.coordinates);

        //---------------------------------------------------------------------------------------------
        imageView.setImageURI(Uri.parse(pathPhoto.getPhotoPath()));

        //---------------------------------------------------------------------------------------------
        textView.setText(Float.toString(pathPhoto.getTemperature()));
        textView2.setText(Float.toString(pathPhoto.getPressure()));
        textView3.setText(pathPhoto.getCoordinates());

        return v;
    }

}
