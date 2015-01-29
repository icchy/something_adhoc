# specify commands for each platform as following
# 1. search AP using wireless card (ex. iwlist scan)
# 2. connect AP using wireless card (ex. iwconfig wlan0 )

import socket


def send(apName, net, host, port, msg):
    net.connect_AP(apName)
    flag = False

    for _ in range(5):
        try:
            net.pingtest(host)
        except:
            print "could not connect to " + apName
            print "try again within 2 seconds..."

            try:
                __import__('time').sleep(2)
            except KeyboardInterrupt:
                break

            continue

        flag = True
        break

    if not flag:
        print "could not connect to " + apName
        return False

    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    try:
        sock.connect((host, port))
    except:
        return False
    try:
        sock.sendall(msg)
    except:
        return False
    sock.close()
    return True
