package common;

import java.util.ArrayList;

public class Filter {

    public static ArrayList<StringPair> filterReferences(ArrayList<String> references, String uri) {

        var filteredReferences = new ArrayList<StringPair>();

        if(references.isEmpty()){

            return filteredReferences;
        }

        String url = "";
        int right_slash = 0;
        for (int i = 0; i < uri.length(); i++) {
            var c = uri.charAt(i);
            url += c;
            if (c == '/') {
                right_slash++;
                if (right_slash == 3) {
                    break;
                }
            }
        }

        for (var ref : references) {

            String imageFormat = "";
            for (int i = ref.length() - 1; i >= 0; i--) {

                if (ref.charAt(i) == '.') {

                    for (int k = i; k < ref.length(); k++) {

                        imageFormat += ref.charAt(k);
                    }
                    break;
                }
            }

            if (ref.contains("http")) {

                filteredReferences.add(new StringPair(ref, imageFormat));
            } else if(ref != "") {

                filteredReferences.add(new StringPair(url + ref, imageFormat));
            }
        }
        return filteredReferences;
    }
}
