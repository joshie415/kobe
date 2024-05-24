package request;

public class CustomParser {

    // extract java useable values from a raw http request string
    // https://developer.mozilla.org/en-US/docs/Web/HTTP/Messages
    public static ParsedRequest parse(String request) {
        String[] lines = request.split("(\r\n|\r|\n)");
        String requestLine = lines[0];
        String[] requestParts = requestLine.split(" ");
        var result = new ParsedRequest();
        result.setMethod(requestParts[0]);

        var parts = requestParts[1].split("\\?");
        result.setPath(parts[0]);

        if (parts.length == 2) {
            System.out.println(parts[1]);
            String[] queryParts = parts[1].split("&");
            for (int i = 0; i < queryParts.length; i++) {
                String[] pair = queryParts[i].split("=");
                result.setQueryParam(pair[0], pair[1]);
            }
        }

        // parse headers and body
        String body = "";
        boolean emptyLine = false;

        for (String line: lines){
            // headers
            if(line.contains(":") && !emptyLine){
                String[] headerParts = line.split(":"); // Cookie: something
                String key = headerParts[0].trim();
                String value = headerParts[1].trim();
                result.setHeaderValue(key, value);

                // Cookie: name=value; name2=value2; name3=value3
                if(key.equalsIgnoreCase("cookie")){
                    // we have a cookie in the header
                    String[] cookieParts = value.split(";");
                    for(String cookiePart: cookieParts){
                        if(cookiePart.isEmpty()){
                            continue;
                        }
                        String[] cookieKeyValue = cookiePart.split("=");
                        result.setCookieValue(cookieKeyValue[0].trim(),
                                cookieKeyValue[1].trim());
                    }
                }
            }

            if(line.equals("")){
                emptyLine = true;
            }
            if(emptyLine){
                body += line;
            }
        }

        //String cookieValue = result.getHeaderValue("Cookie")

        result.setBody(body);
        return result;
    }
}
