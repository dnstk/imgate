package tk.dnstk.imgate.api.security;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import tk.dnstk.imgate.api.InvalidAccessException;

import java.util.Collections;
import java.util.Set;

class AuthorizedOnConverterHook implements ConditionalGenericConverter {

    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        return targetType.hasAnnotation(AuthorizedOn.class);
    }

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new ConvertiblePair(String.class, String.class));
    }

    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        AuthorizedOn authorizedOn = targetType.getAnnotation(AuthorizedOn.class);
        SecurityValue sv = authorizedOn.value();
        String svv = SecurityContext.getContext().get(sv);
        if (svv == null || !svv.equals(source)) {
            throw new InvalidAccessException(
                    String.format("No access to %s:%s, current %s is: %s", sv, source, sv, svv));
        } else {
            return source;
        }
    }

}
