package farszownicy.caldirola.agendacalendar.render;

import android.view.View;

import androidx.annotation.LayoutRes;

import java.lang.reflect.ParameterizedType;

import farszownicy.caldirola.models.BaseCalendarEntry;

/**
 * Base class for helping layout rendering
 */
public abstract class EntryRenderer<T extends BaseCalendarEntry> {
    public abstract void render(final View view, final T event);

    @LayoutRes
    public abstract int getEventLayout();

    public Class<T> getRenderType() {
        ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
        return (Class<T>) type.getActualTypeArguments()[0];
    }
}
