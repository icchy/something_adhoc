import parser
import wifiCommands

class Relay:
    myNode = ""
    srcNode = ""
    distNode = ""
    relayNode = []
    prefix = ""
    msg = ""
    net = None

    def __init__(self, net, data):
        self.net = net
        self.myNode = net.myNode
        print "myNode:" + self.myNode
        if self.parse(data):
            self.relay()
        else:
            print "parse failed"

    def create_message(self, srcNode, distNode, relayNode, msg):
        self.msg = srcNode + ":" + ":".join(relayNode) + "::" + distNode + ":" + msg
        return self.msg

    def parse(self, msg):
        res = parser.parser(msg)
        if res[0]:
            self.srcNode = res[1]
            self.distNode = res[2]
            self.relayNode = res[3]
            self.msg = res[4]
            return True
        else:
            return False

    def relay(self):
        if self.srcNode and self.distNode and self.relayNode:
            if self.distNode == self.myNode: # here is distination
                receive(self.srcNode, self.msg)
            elif self.srcNode == self.myNode: # here is srcNode
                send(self.net, create_message(self.srcNode, self.distNode, [], self.msg), [])
            else: # just relay
                send(self.net, create_message(self.srcNode, self.distNode, self.relayNode + [self.myNode], self.msg), (self.srcNode + self.relayNode))
        else:
            print "something wrong. check format of message."





def send(net, msg, pastNode):
    aps = net.get_APs()
    for ap in aps:
        if ap not in pastNode and parser.get_prefix(ap):
            wifiCommands.send(ap, net, net.default_host, net.default_port, msg)

def receive(srcNode, msg):
    print "received message from %s : %s"%(srcNode, msg)


        # f = open("/tmp/something_route_" + __import__('random').randint(10000, 99999), "w")
        # self.myNodeName = myNodeName



    # currently not implemented

    # def getRoute(nodeName):
    #     pass
    #
    # def getAllRoute():
    #     s = open('/tmp/')
