import sys, platform, readline, os

from osnetwork import OSNetwork
from server import Server

import logging


def main():
    wifiInf = raw_input("Enter Wifi interface: ").strip()
    command = ""
    net = OSNetwork(wifiInf)
    macaddr = ''.join(__import__('netifaces').ifaddresses(wifiInf)[17][0]['addr'].upper().split(':'))

    net.set_Adhoc("senshin_" + macaddr)

    daemon = Server('0.0.0.0', 13337)

    while command != "exit":
        try:
            command = raw_input("input command(send, scan, exit): ")
        except EOFError:
            break

        if command == "send":
            dist = raw_input("input target SSID: ").strip()
            net.connect_AP(dist)
            try:
                if not net.pingtest("192.168.20.1"):
                    print "could not connect to " + dist
            except AssertionError:
                print "could not connect to " + dist

            msg = raw_input("input message: ").strip()

            continue


        if command == "scan":
            aps = net.get_APs()
            for ap in aps:
                print "SSID: " + ap.ssid

            continue

        print "unknown command: " + command

    print "Bye!"



if __name__ == "__main__":
    histfile = os.path.join(os.path.expanduser("~"), ".pyhist")
    try:
        readline.read_history_file(histfile)
    except IOError:
        pass
    import atexit
    atexit.register(readline.write_history_file, histfile)
    del os, histfile

    main()
