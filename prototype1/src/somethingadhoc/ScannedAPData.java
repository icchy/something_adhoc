package somethingadhoc;

import java.util.Date;

/*
    SSID                              BSSID               MODE             FREQ       RATE       SIGNAL   SECURITY   ACTIVE
        'ggwp'                            02:11:87:DA:DD:57   Ad-Hoc           2412 MHz   54 MB/s    0        --         no
*/
public class ScannedAPData {
    Date scanOn;
    String ssid;
    String bssid;
    String mode;
    String freq;
    String rate;
    String signal;
    String security;
    String active;
}
