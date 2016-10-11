package architect.jazzy.medicinereminder.MedicalAssistant.Fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import architect.jazzy.medicinereminder.MedicalAssistant.Adapters.ImageAdapter;
import architect.jazzy.medicinereminder.R;

/**
 * Created by Jibin_ism on 07-Jun-15.
 */
public class EmojiSelectFragment extends DialogFragment {

  Context mContext;
  GridView gridView;

  DialogFragment fragment;
  OnFragmentInteractionListener mListener;


  public EmojiSelectFragment() {
    super();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    View v = inflater.inflate(R.layout.fragment_emoji_select, null);
    try {
      ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    } catch (NullPointerException e) {
      e.printStackTrace();
    }
    fragment = this;
    mContext = getActivity().getApplicationContext();
    gridView = (GridView) v.findViewById(R.id.gridviewEmoji);
    gridView.setAdapter(new ImageAdapter(mContext));
    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mListener.onEmojiSelected(position);
        fragment.dismiss();
      }
    });

    getDialog().setTitle("Select Medicine Icon");
    return v;
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity)
    ;
    try {
      mListener = (OnFragmentInteractionListener) activity;
    } catch (ClassCastException e) {
      throw new ClassCastException(activity.toString()
          + " must implement OnFragmentInteractionListener");
    }
  }

  @Override
  public void onResume() {
    super.onResume();
  }

  @Override
  public void onPause() {
    super.onPause();
  }

  @Override
  public void onDetach() {
    super.onDetach();
    mListener = null;
  }

  public interface OnFragmentInteractionListener {
    void onEmojiSelected(int position);
  }


}
