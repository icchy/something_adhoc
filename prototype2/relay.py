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

    def __init__(self, net):
        self.net = net
        self.myNode = net.myNode

    def create_message(self, srcNode, distNode, relayNode, msg):
        if len(relayNode):
            self.msg = srcNode + ":" + ":".join(relayNode) + "::" + distNode + ":" + msg
        else:
            self.msg = srcNode + "::" + distNode + ":" + msg
        return self.msg

    def send(self, distNode, msg):
        send(self.net, self.create_message(self.net.myNode, distNode, self.relayNode, msg), [self.srcNode])

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
        print self.srcNode, self.distNode, self.relayNode, self.myNode
        if self.srcNode and self.distNode:
            if self.distNode == self.myNode: # here is distination
                receive(self.srcNode, self.msg)
            else: # just relay
                if self.net.debug:
                    print "relaying..."
                send(self.net, self.create_message(self.srcNode, self.distNode, self.relayNode + [self.myNode], self.msg), (self.srcNode + self.relayNode))



def send(net, msg, pastNode):
    try:
        strictedNode = open('strictedAddr').read().strip().split('\n')
        pastNode = pastNode + strictedNode
    except:
        pass

    aps = net.get_APs()
    for ap in aps:
        pre = parser.get_prefix(ap)
        if ap not in pastNode and pre and pre in ap:
            if wifiCommands.send(ap, net, net.default_host, net.default_port, msg): return True
    if net.debug:
        print "failed to send"


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
