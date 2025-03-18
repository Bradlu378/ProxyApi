package foxo.flanty.proxyApi;


import java.util.UUID;

public class Test {
    public static void main(String[] args) {
        String nick = "Flanty";
        String key = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAPEfZZdsXO5syEWf" +
                     "rkcFW8DiTH7vkzvmALm0csqn3pe11XtYU/o9uTb0gIWoMZqP+cUAj2ct4fBcXvlE" +
                     "1WR/dsspoC3QAMssCu0vx3RCzd1r2LO9aSCPCyu3YV69p0KqoopIPz8E6+UcngOv" +
                     "05nDNDl6I8h1O8xsuYtbo4S5/dmRAgMBAAECgYAv0X8vQYFIk3BARoOkeA6TOQ3c" +
                     "mpyDY4C/U2b0g8gkJB57QLAez4Dcwi5+0zVLJsRs6kiGONY2+cqn+purO5AOJd6k" +
                     "0WvTvuZAfOzAZUMb5RfFykJAxAlL27zqMgEiyk9Oiw3jExHdxl6ZtH3ygazskgxs" +
                     "7O/SIbCxNiCfqs1yQQJBAPlMXkDSryroDMQrG6ZpbK8hjpcK8LtxTbDHWs3N6qWa" +
                     "6XRgU+VOSpwlG3bzkBsy9Y1HRUH+CbS6Jm4Xay5/DdkCQQD3msPWoKm6ivJ6mGaE" +
                     "QPaXz1/ioSzi01M4rOz40wFnX9BeY+i4qU53pAWlvxrHv28At0QpwJr/9CY3eQmK" +
                     "6/55AkEA7K5AXbKoc0fTtvpmSduVP7/QLD5KxuqPI+Jgpzt0Jr2oHsDFZdKqqwvf" +
                     "w1MB4ZDD59leO4T/mgGRMrwxGfeuIQJAH3D1oVFxfBCczdiAXBbruUdKA7s3ue3f" +
                     "yXib9tTEft351aKWMKy/HA4l8XAc9HRoogyjiTG9/PaBy9WA0rU48QJBAIdSGVHY" +
                     "qn+zGYfO0g7ldKrkUaWfo3HWOmCge5cPkWWvMNyl4jSOmMt7T/eNE1zCNopPnMG6" +
                     "8Vgx0gAFC5KE5J4=";
        System.out.println(UUID.nameUUIDFromBytes((nick+key).getBytes()));
    }

}
