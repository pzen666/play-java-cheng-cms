package actions;

import play.Environment;
import play.inject.BindingKey;
import play.libs.typedmap.TypedKey;
import play.libs.typedmap.TypedMap;

public class AttrKey {
    public static final TypedKey<String> USERNAME = TypedKey.create("username");
}