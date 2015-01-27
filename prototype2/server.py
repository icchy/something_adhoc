import socket
import relay

class Server:
    def __init__(self, net, host, port):
        self.net = net
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        self.sock.bind((host, port))
        self.sock.listen(1)

    def wait(self):
        print "myNode : " + self.net.myNode
        print "Launching server... press Ctrl-C to quit relay mode"

        try:
            self.csock, self.addr = self.sock.accept()
        except KeyboardInterrupt:
            self.sock.close()
            return False

        data = ""
        while True:
            try:
                tmp = self.csock.recv(1024)
            except:
                break
            if not tmp:
                break
            data += tmp

        print "received %d bytes data: "%(len(data)) + repr(data)

        rel = relay.Relay(self.net)
        rel.relay()

        return True

    def __enter__(self):
        return self

    def __exit__(self, type, value, traceback):
        self.sock.close()
        try:
            self.csock.close()
        except:
            pass
