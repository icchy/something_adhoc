import sys, platform, readline, os

from osnetwork import OSNetwork
from server import Server
import wifiCommands
import logging

prefix = "senshin"
macaddr = ""

default_host = "192.168.20.1"
default_client = "192.168.20.10"
default_port = 13337


def main():
    wifiInf = raw_input("Enter Wifi interface: ").strip()
    command = ""
    net = OSNetwork(wifiInf)
    macaddr = ''.join(__import__('netifaces').ifaddresses(wifiInf)[17][0]['addr'].upper().split(':'))

    net.set_Adhoc(prefix + "_" + macaddr)

    while command != "exit":
        with Server('0.0.0.0', default_port) as serv:
            if serv.wait():
                continue

        try:
            command = raw_input("input command(scan, send, exit): ")
        except EOFError:
            break

        if command == "scan":
            aps = net.get_APs()
            for ap in aps:
                print "SSID: " + ap.ssid
            continue

        if command == "send":
            dist = raw_input("input target SSID: ").strip()
            msg = raw_input("input message: ").strip()
            res = wifiCommands.send(dist, net, default_host, default_port, msg)

            if not res:
                print "sending failed"

            continue


        print "unknown command: " + command

    print "Bye!"

def onConnected():
    print "called"
    



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
