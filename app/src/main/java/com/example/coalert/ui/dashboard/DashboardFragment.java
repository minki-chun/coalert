package com.example.coalert.ui.dashboard;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.icu.text.UnicodeSetSpanner;
import android.media.Image;
import android.os.Bundle;
import android.util.FloatMath;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.textservice.TextInfo;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.example.coalert.SharedViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.example.coalert.R;
import com.example.coalert.ui.home.HomeFragment;

public class DashboardFragment extends Fragment implements View.OnTouchListener {

    private DashboardViewModel dashboardViewModel;
    private boolean startmap = true;
    private float offset_x, offset_y, offset_x2, offset_y2;
    private int isMoving;
    private final int NONE = 0;
    private final int DRAG = 1;
    private final int ZOOM = 2;
    private int mode = NONE;
    private float oldDist = 1f;
    private float newDist = 1f;
    TextView maptx;
    ImageView map;
    ImageView image3;
    private int margin;
    int parentWidth;
    int parentHeight;
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();
    PointF start = new PointF();
    PointF mid = new PointF();
    private static final float MIN_ZOOM = 0.1f;
    private static final float MAX_ZOOM = 8.0f;
    FrameLayout mapframe;
    float old_X;
    float old_Y;
    boolean first = true;
    public SharedViewModel sharedViewModel;
    Switch mapsw;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        View root2 = inflater.inflate(R.layout.activity_banner, container, false);
        map = (ImageView) root.findViewById(R.id.mapview);
        mapsw = (Switch) root.findViewById(R.id.switch2);
        maptx = (TextView) root.findViewById(R.id.maptextView);
        image3 = root2.findViewById(R.id.imageView3);
        mapframe = root.findViewById(R.id.mapviewframe);
        margin = image3.getHeight();

        map.setOnTouchListener(this);
        if(first){
            map.setScaleType(ImageView.ScaleType.MATRIX);
            matrix.setScale(0.2f,0.2f);
            matrix.postTranslate(-500, -500);
            map.setImageMatrix(limitZoom(matrix));
            first = false;
        }

        mapsw.setOnCheckedChangeListener(new nML());
        return root;
    }

    class nML implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked) {
                sharedViewModel.setHere(true);
                map.setScaleType(ImageView.ScaleType.MATRIX);
                matrix.setScale(0.5f, 0.5f);
                matrix.postTranslate(-2800, -1400);
                map.setImageMatrix(limitZoom(matrix));
            }
            else{
                sharedViewModel.setHere(false);
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        if(sharedViewModel.getHere()){mapsw.setChecked(true);}
        else {mapsw.setChecked(false);}
        if(sharedViewModel.getSettin()){
            sharedViewModel.setHere(false);
            if(sharedViewModel.getSmallloc().equals("강남구")) {
                map.setScaleType(ImageView.ScaleType.MATRIX);
                matrix.setScale(0.5f, 0.5f);
                matrix.postTranslate(-2800, -2600);
                map.setImageMatrix(limitZoom(matrix));}
            else if(sharedViewModel.getSmallloc().equals("노원구")){
                map.setScaleType(ImageView.ScaleType.MATRIX);
                matrix.setScale(0.5f, 0.5f);
                matrix.postTranslate(-3000, -700);
                map.setImageMatrix(limitZoom(matrix));}
            else if(sharedViewModel.getSmallloc().equals("중구")){
                map.setScaleType(ImageView.ScaleType.MATRIX);
                matrix.setScale(0.5f, 0.5f);
                matrix.postTranslate(-2300, -1700);
                map.setImageMatrix(limitZoom(matrix));}
            else if(sharedViewModel.getSmallloc().equals("광진구")){
                map.setScaleType(ImageView.ScaleType.MATRIX);
                matrix.setScale(0.5f, 0.5f);
                matrix.postTranslate(-1400, -2800);
                map.setImageMatrix(limitZoom(matrix));}
            else if(sharedViewModel.getSmallloc().equals("동대문구")){
                map.setScaleType(ImageView.ScaleType.MATRIX);
                matrix.setScale(0.5f, 0.5f);
                matrix.postTranslate(-2800, -1400);
                map.setImageMatrix(limitZoom(matrix));}
            else{
                map.setScaleType(ImageView.ScaleType.MATRIX);
                matrix.setScale(0.2f, 0.2f);
                matrix.postTranslate(-500, -500);
                map.setImageMatrix(limitZoom(matrix));
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event){
        ImageView view = (ImageView) v;
        view.setScaleType(ImageView.ScaleType.MATRIX);
        float scale;

        int act = event.getAction();
        parentWidth = ((ViewGroup)v.getParent()).getWidth();
        parentHeight = ((ViewGroup)v.getParent()).getHeight();
        int width = ((ViewGroup) v.getParent()).getWidth() - v.getWidth();
        int height = ((ViewGroup) v.getParent()).getHeight() - v.getHeight();

        switch (act & MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                offset_x = event.getX();
                offset_y = event.getY();
                start.set(offset_x, offset_y);
                mode = DRAG;
                if(sharedViewModel.getHere()){
                    maptx.setText("[동대문구청]308번 확진자발생, ddm4you.blog.me 참조하시고, 12월3일은 수능일입니다. 관내 학교주변 단체응원, 각종모임, 행사 자제 부탁드립니다");
                }
                else{
                    maptx.setText("[마포구청] 확진자 6명 발생. 역학조사 진행 중이며 이동동선은 추후 홈페이지 및 블로그 참고바랍니다. blog.naver.com/prmapo77");
                }


                return true;

            case MotionEvent.ACTION_POINTER_DOWN:
                old_X = event.getX();
                old_Y = event.getY();
                newDist = spacing(event);
                oldDist = spacing(event);
                if(oldDist>5f){
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                }
                return true;

            case MotionEvent.ACTION_UP:
                break;

            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;



            case MotionEvent.ACTION_MOVE:
                sharedViewModel.setHere(false);
                mapsw.setChecked(false);
                if(mode == DRAG){
                    matrix.set(savedMatrix);
                    if (view.getLeft() >= -392){
                        matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);
                    }
                    //v.setX(v.getX() + (event.getX()) - (float) offset_x);
                    //v.setY(v.getY() + (event.getY()) - (float) offset_y);
                    //offset_x2 = event.getX();
                    //offset_y2 = event.getY();
                    //if(Math.abs(offset_x2-offset_x)>20 || Math.abs(offset_y2-offset_y)>20) {
                    //    offset_x = offset_x2;
                    //    offset_y = offset_y2;
                    //}
                }
                else if(mode == ZOOM){
                    newDist = spacing(event);
                    if(newDist > 5f){
                        matrix.set(savedMatrix);
                        scale = newDist / oldDist;
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                    //if (newDist - oldDist > 20) {
                    //    oldDist = newDist;
                    //}
                    //else if(oldDist - newDist > 20){
                    //    oldDist = newDist;
                   // }
                }
                ((ImageView) v).setImageMatrix(limitZoom(matrix));
                return true;
            case MotionEvent.ACTION_CANCEL:
            default:
                break;
        }
        return false;
    }
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);

        return (float)Math.sqrt(x * x + y * y);
    }
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }
    private Matrix limitZoom(Matrix m) {
        float[] values = new float[9];
        m.getValues(values);
        float scaleX = values[Matrix.MSCALE_X];
        float scaleY = values[Matrix.MSCALE_Y];
        int viewWidth = getResources().getDisplayMetrics().widthPixels;
        int viewHeight = getResources().getDisplayMetrics().heightPixels;

        if(scaleX > MAX_ZOOM) {
            scaleX = MAX_ZOOM;
        } else if(scaleX < MIN_ZOOM) {
            scaleX = MIN_ZOOM;
        }

        if(scaleY > MAX_ZOOM) {
            scaleY = MAX_ZOOM;
        } else if(scaleY < MIN_ZOOM) {
            scaleY = MIN_ZOOM;
        }

        values[Matrix.MSCALE_X] = scaleX;
        values[Matrix.MSCALE_Y] = scaleY;
        m.setValues(values);

        return m;
    }
}