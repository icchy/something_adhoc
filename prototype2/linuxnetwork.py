from osnetwork import OSNetwork
import commands

class LinuxNetWork(OSNetwork):
    def __init__(self, osnetwork):
        check = self.checkRequirements()
        self.wifiInf = osnetwork.wifiInf

    def checkRequirements(self):
        # 1. run as root?
        assert isPrivileged(), "you must run as root"

        # 2. have required programs
        cmd_list = ["ifconfig", "iwconfig", "iwlist"]
        res = hasCommand(cmd_list)
        assert res == True, str(res) + ("is" if len(res)==1 else "are") +" not implemented"

        # 3. have wifi interface
        assert hasWiFiInterface(self.wifiInf), self.wifiInf + " does not exits"


    def get_APs(self):
        return wifi.Cell.all(self.wifiInf)

    def set_Adhoc(self, adhocName):
        cmds = ["ifconfig %s down"%(self.wifiInf),
        "iwconfig %s mode ad-hoc"%(self.wifiInf),
        "iwconfig %s essid %s"%(self.wifiInf, adhocName),
        "ifconfig %s up"%(self.wifiInf),
        "ifconfig %s 192.168.1.1"%(self.wifiInf)]
        execCmds(cmds)

    def get_if(self):
        return __import__('netifaces').interfaces()

    def connect_AP(self):
        pass


def hasCommand(cmd):
    if type(cmd) == type(""):
        return commands.getstatusoutput("which " + cmd)[0] == 0
    elif type(cmd) == type([]):
        not_have_commands = filter(lambda x:not hasCommand(x), cmd)
        if len(not_have_commands) == 0:
            return True
        else:
            return not_have_commands

def execCmds(cmds):
    for cmd in cmds:
        res = commands.getstatusoutput(cmd)
        assert res[0]==0, "Command execution fail (%s):\n"%(res[0]) + res[1]

def isPrivileged():
    return __import__('os').getuid() == 0

def hasWiFiInterface(infName):
    return infName in commands.getoutput("ifconfig")
