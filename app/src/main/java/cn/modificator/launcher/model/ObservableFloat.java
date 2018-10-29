package cn.modificator.launcher.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Observable;

/**
 * Created by Modificator
 * time: 16/12/3.上午2:00
 * des:create file and achieve model
 */

public class ObservableFloat extends Observable implements Parcelable, Serializable {
  static final long serialVersionUID = 1L;
  private float mValue;

  public ObservableFloat(float mValue) {
    this.mValue = mValue;
  }

  public ObservableFloat() {
  }

  public float get() {
    return mValue;
  }

  public void set(float value) {
    if (mValue != value)
      this.mValue = value;
    notifyObservers(mValue);
    setChanged();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeFloat(mValue);
  }

  public static final Parcelable.Creator<ObservableFloat> CREATOR
      = new Parcelable.Creator<ObservableFloat>() {

    @Override
    public ObservableFloat createFromParcel(Parcel source) {
      return new ObservableFloat(source.readFloat());
    }

    @Override
    public ObservableFloat[] newArray(int size) {
      return new ObservableFloat[size];
    }
  };
}
