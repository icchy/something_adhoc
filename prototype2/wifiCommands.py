# specify commands for each platform as following
# 1. search AP using wireless card (ex. iwlist scan)
# 2. connect AP using wireless card (ex. iwconfig wlan0 )

import socket


def send(apName, net, host, port, msg):
    net.connect_AP(apName)
    flag = False

    for _ in range(5):
        if not net.pingtest(host):
            print "could not connect to " + apName
            print "try again within 2 seconds..."
            try:
                __import__('time').sleep(2)
            except KeyboardInterrupt:
                break
        else:
            flag = True
            break

    if not flag:
        print "could not connect to " + apName
        return False

    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock.connect((host, port))
    try:
        sock.sendall(msg)
    except:
        return False
    sock.close()
    return True
