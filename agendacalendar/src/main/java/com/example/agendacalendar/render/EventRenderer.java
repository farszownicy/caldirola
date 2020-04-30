package com.example.agendacalendar.render;

import android.view.View;

import androidx.annotation.LayoutRes;

import com.example.agendacalendar.models.CalendarEvent;

import java.lang.reflect.ParameterizedType;

/**
 * Base class for helping layout rendering
 */
public abstract class EventRenderer<T extends CalendarEvent> {
    public abstract void render(final View view, final T event);

    @LayoutRes
    public abstract int getEventLayout();

    public Class<T> getRenderType() {
        ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
        return (Class<T>) type.getActualTypeArguments()[0];
    }
}
