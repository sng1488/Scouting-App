package org.usfirst.frc.team25.scouting.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.usfirst.frc.team25.scouting.R;

import java.math.BigDecimal;

/**
 * Created by sng on 1/31/2018.
 */

public class ButtonTimer extends RelativeLayout {

    public final Button startStopButton;
    private final Button incButton;
    private final Button decButton;
    private final TextView titleView;
    private TextView valueView;
    private float incDecAmount;
    private float minValue;
    private float maxValue;
    private boolean isTimerStart;

    private OnClickListener listener;

    public ButtonTimer(Context c, AttributeSet attrs) {
        super(c, attrs);
        initializeViews(c);
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ButtonTimer);

        //XML attributes that can be set in layout files, rather than programmatically
        valueView = findViewById(R.id.button_timer_value);
        incButton = findViewById(R.id.timer_inc_button);
        decButton = findViewById(R.id.timer_dec_button);
        startStopButton = findViewById(R.id.start_stop_button);
        titleView = findViewById(R.id.button_timer_title);


        setValue(typedArray.getFloat(R.styleable.ButtonTimer_initialValueTimer, 0));
        setTitle(typedArray.getString(R.styleable.ButtonTimer_titlePromptTimer));
        setMinValue(typedArray.getFloat(R.styleable.ButtonTimer_minValueTimer, 0));
        setMaxValue(typedArray.getFloat(R.styleable.ButtonTimer_maxValueTimer, Float.MAX_VALUE));
        setIncDecAmount(typedArray.getFloat(R.styleable.ButtonTimer_buttonIncDecAmount, 0.5f));

        typedArray.recycle();

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            if (listener != null) listener.onClick(this);
        }

        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP && (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER || event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
            if (listener != null) listener.onClick(this);
        }
        return super.dispatchKeyEvent(event);
    }

    public void setOnClickListener(OnClickListener listener) {
        this.listener = listener;
    }

    private void initializeViews(Context c) {
        LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.button_timer_view, this);
    }

    /**
     * @param title - title of the left TextView
     */
    private void setTitle(CharSequence title) {
        titleView.setText(title);
    }

    /**
     * @return Float value of the displayed number
     */
    public float getValue() {
        valueView = findViewById(R.id.button_timer_value);
        String value = valueView.getText().toString().split(" ")[0];
        return Float.parseFloat(value);
    }

    /**
     * @param value - the initial value of the integer between the buttons. Cannot be less than 0.
     */
    public void setValue(float value) {
        if (value > maxValue)
            value = maxValue;

        if (value < minValue)
            value = minValue;

        BigDecimal bd = new BigDecimal(value);


        //Rounds the decimal to the tenths place
        String displayText = bd.setScale(1, BigDecimal.ROUND_HALF_EVEN).toPlainString() + " sec";
        valueView = findViewById(R.id.button_timer_value);
        valueView.setText(displayText);
    }

    public void setIncDecAmount(float incDecAmount) {
        this.incDecAmount = incDecAmount;
    }

    private void setMinValue(float minValue) {
        this.minValue = minValue;
    }

    private void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        runTimer();
        isTimerStart = false;


        //Listeners to increment and decrement the value with a click
        incButton.setOnClickListener(view -> increment());
        decButton.setOnClickListener(view -> decrement());

        //Essentially acts like a toggle, starting and stopping the timer
        startStopButton.setOnClickListener(view -> {
            if (isTimerStart) { //timer already started, need to stop
                startStopButton.setText("Start");
                incButton.setEnabled(true);
                decButton.setEnabled(true);
            } else { // starts timer
                startStopButton.setText(("Stop"));
                setValue(0f);

                incButton.setEnabled(false);
                decButton.setEnabled(false);
            }
            isTimerStart = !isTimerStart;
        });
    }

    private void runTimer() {
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (isTimerStart)
                    setValue(getValue() + 0.1f);
                handler.postDelayed(this, 100);
            }
        });

    }

    private void increment() {
        setValue(getValue() + incDecAmount);
    }

    private void decrement() {
        setValue(getValue() - incDecAmount);
    }
}
