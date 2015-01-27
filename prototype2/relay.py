import parser
import wifiCommands
import main

class Relay:
    myNode = ""
    srcNode = ""
    distNode = ""
    relayNode = []
    prefix = ""
    msg = ""
    net = None

    def __init__(self):
        import main
        self.myNode = main.prefix + "_" + main.macaddr
        self.net = main.net


    def create_message(self, srcNode, distNode, relayNode, msg):
        self.msg = srcNode + ":" + ":".join(relayNode) + "::" + distNode + ":" + msg
        pass

    def parse(self, msg):
        res = parser.parser(msg)
        if res[0]:
            srcNode = res[1]
            distNode = res[2]
            relayNode = res[3]
            self.msg = res[4]
            return True
        else:
            return False

    def relay(self, msg):
        pass

    def send(self, net, msg):
        aps = self.net.get_APs()
        for ap in aps:
            if parser.get_prefix(ap):
                wifiCommands.send(ap, main.net, main.default_host, main.default_port, msg)




        
            






        # f = open("/tmp/something_route_" + __import__('random').randint(10000, 99999), "w")
        # self.myNodeName = myNodeName



    # currently not implemented

    # def getRoute(nodeName):
    #     pass
    #
    # def getAllRoute():
    #     s = open('/tmp/')
