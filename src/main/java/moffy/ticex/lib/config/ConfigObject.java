package moffy.ticex.lib.config;

import java.util.*;
import java.util.stream.Collectors;

public class ConfigObject extends ArrayList<String> {

    public ConfigObject(String ...list){
        super(List.of(list));
    }

    public Map<String, Object> toNestedMap() {
        Map<String, Object> root = new LinkedHashMap<>();

        for (String line : this) {
            if (line == null || line.isEmpty()) {
                continue;
            }

            String[] parts = line.split("\\|");

            if (parts.length < 2) {
                throw new IllegalArgumentException(
                        "Invalid format. Expected key|value format: " + line
                );
            }

            Map<String, Object> current = root;

            for (int i = 0; i < parts.length - 2; i++) {
                String key = parts[i];

                Object next = current.get(key);

                if (next == null) {
                    Map<String, Object> child = new LinkedHashMap<>();
                    current.put(key, child);
                    current = child;
                } else if (next instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> child = (Map<String, Object>) next;
                    current = child;
                } else {
                    throw new IllegalArgumentException(
                            "Key conflict. Existing value is String, but Map is required: " + key
                    );
                }
            }

            String lastKey = parts[parts.length - 2];
            String value = parts[parts.length - 1];

            Object existing = current.get(lastKey);
            if (existing instanceof Map) {
                throw new IllegalArgumentException(
                        "Key conflict. Existing value is Map, but String is required: " + lastKey
                );
            }

            current.put(lastKey, Integer.valueOf(value));
        }

        return root;
    }

    public Optional<Integer> getConfiguredValue(Object ...keys){
        try{
            for(String line : this){
                String[] parsedLine = line.split("\\|");
                if(keys.length == parsedLine.length - 1){
                    boolean matched = true;
                    for(int i = 0; i < keys.length; i++){
                        if(!keys[i].toString().equals(parsedLine[i])){
                            matched = false;
                            break;
                        }
                    }

                    if(matched){
                        return Optional.of(Integer.valueOf(parsedLine[parsedLine.length - 1]));
                    }
                }
            }
        }catch (Exception e){
            return Optional.empty();
        }
        return Optional.empty();
    }
}
