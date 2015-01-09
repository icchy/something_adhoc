from socket import socket
import threading

class Server:
    def __init__(self, addr, port):
        self.addr = addr
        self.port = port


    def run(self):
        self.msg = ""
        self.th = threading.Thread(target=self.upserver)
        self.th.setDaemon(True)
        self.th.start()

    def upserver(self):
        self.sock = socket(AF_INET, SOCK_STREAM)
        self.sock.bind((self.addr, self.port))
        self.sock.listen(5)
        self.flag = True
        csock, addr = self.sock.accept()
        while True:
            data = csock.recv(1024)
            if not data:
                break
            self.msg += data

        self.flag = False

    def stop(self):
        self.flag = False
        self.sock.close()

    def is_running(self):
        return self.flag
    
    def get_msg(self):
        return self.msg
