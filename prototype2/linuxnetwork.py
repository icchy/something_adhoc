from osnetwork import OSNetwork
import commands
import wifi, netifaces

class LinuxNetWork(OSNetwork):
    def __init__(self, osnetwork):
        check = self.checkRequirements()
        self.stop_networkmng()
        self.wifiInf = osnetwork.wifiInf
        # cmds = ["ip addr add 192.168.1.1 dev wlan0"]

    def checkRequirements(self):
        # 1. run as root?
        assert isPrivileged(), "you must run as root"

        # 2. have required programs
        cmd_list = ["ifconfig", "ip", "iw"]
        res = hasCommand(cmd_list)
        assert res == True, str(res) + ("is" if len(res)==1 else "are") +" not implemented"

        # 3. have wifi interface
        assert hasWiFiInterface(self.wifiInf), self.wifiInf + " does not exits"

    def stop_networkmng(self):
        cmds = ["service network-manager stop"]
        execCmds_force(cmds)

    def downAP(self):
        cmds = ["ip link set %s down"%(self.wifiInf)]
        execCmds(cmds)

    def upAP(self):
        cmds = ["ip link set %s up"%(self.wifiInf)]
        execCmds(cmds)

    def get_APs(self):
        self.upAP()
        try:
            return wifi.Cell.all(self.wifiInf)
        except wifi.exceptions.InterfaceError:
            pass

    def set_Adhoc(self, adhocName):
        self.delIP("192.168.20.10")
        self.downAP()
        # cmds = ["ifconfig %s 192.168.1.1 netmask 255.255.255.0"%(self.wifiInf),
        #         "iwconfig %s essid %s mode ad-hoc channel auto"%(self.wifiInf, adhocName)]
        cmds = ["iw dev %s set type ibss"%(self.wifiInf)]
        execCmds(cmds)
        self.upAP()
        cmds = ["iw dev %s ibss join %s 2412"%(self.wifiInf, adhocName)]
        execCmds(cmds)
        self.addIP("192.168.20.1")

    def get_if(self):
        return __import__('netifaces').interfaces()

    def connect_AP(self, apName):
        self.delIP("192.168.20.1")
        self.downAP()
        # cmds = ["iwconfig %s essid %s mode ad-hoc channel auto"%(self.wifiInf, apName),
        #         "ifconfig %s 192.168.1.10 netmask 255.255.255.0"%(self.wifiInf)]
        cmds = ["iw dev %s set type ibss"%(self.wifiInf),
                "iw dev %s ibss join %s 2412"%(self.wifiInf, apName)]
        execCmds(cmds)
        self.addIP("192.168.20.10")

    def pingtest(self, ip):
        cmds = ["ping -c 3 %s"%(ip)]
        execCmds(cmds)

    def delIP(self, ip):
        cmds = ["ip addr del %s/24 dev %s"%(ip, self.wifiInf)]
        execCmds_force(cmds)

    def addIP(self, ip):
        cmds = ["ip addr add %s/24 dev %s"%(ip, self.wifiInf)]
        execCmds_force(cmds)

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
        assert res[0]==0, "Command execution fail (%s):\n"%(cmd) + res[1]

def execCmds_force(cmds):
    try:
        execCmds(cmds)
    except AssertionError:
        pass

def isPrivileged():
    return __import__('os').getuid() == 0

def hasWiFiInterface(infName):
    return infName in commands.getoutput("ifconfig")
