import socket

class Server:
    def __init__(self, host, port):
        print "called init"
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        self.sock.bind((host, port))
        self.sock.listen(1)

    def wait(self):
        print "Launching server... press Ctrl-C to quit relay mode"

        try:
            self.csock, self.addr = self.sock.accept()
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

            return True

        except KeyboardInterrupt:
            self.sock.close()

            return False

    def __enter__(self):
        return self

    def __exit__(self, type, value, traceback):
        self.sock.close()
        try:
            self.csock.close()
        except:
            pass
