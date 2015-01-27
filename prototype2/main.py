import sys, platform, readline, os

from osnetwork import OSNetwork
from server import Server
import wifiCommands
import logging

default_host = "192.168.20.1"
default_client = "192.168.20.10"
default_port = 13337

prefix = "senshin"

def main():
    try:
        wifiInf = raw_input("Enter Wifi interface: ").strip()
    except:
        sys.exit(2)
    command = ""
    net = OSNetwork(wifiInf)
    net.macaddr = ''.join(__import__('netifaces').ifaddresses(wifiInf)[17][0]['addr'].upper().split(':'))
    net.prefix = prefix
    net.myNode = net.prefix + "_" + net.macaddr
    net.default_host = default_host
    net.default_client = default_client
    net.default_port = default_port

    net.set_Adhoc(prefix + "_" + net.macaddr)

    while command != "exit":
        with Server(net, '0.0.0.0', default_port) as serv:
            if serv.wait():
                continue

        try:
            command = raw_input("input command(scan, send, relay, exit): ")
        except:
            break

        if command == "scan":
            aps = net.get_APs()
            for ap in aps:
                print "SSID: " + ap.ssid
            continue

        if command == "send":
            dist = raw_input("input target SSID: ").strip()
            msg = raw_input("input message: ").strip()
            __import__('relay').Relay(net, msg)

            continue

        if command == "relay":
            continue

        if command != "exit":
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
