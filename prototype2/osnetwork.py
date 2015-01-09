import platform
import commands


class OSNetwork:
    curOS = ""
    wifiInf = ""

    def __init__(self, wifiInf):
        self.wifiInf = wifiInf

        self.curOS = getOS()
        if self.curOS == "Linux":
            from linuxnetwork import LinuxNetWork
            self.net = LinuxNetWork(self)
        elif self.curOS == "Darwin":
            self.net
        elif self.curOS == "Windows":
            self.net
        else:
            raise NotImplementedError("%(self.curOS) is not supported")


    def checkRequirements(self):
        if self.__class__.__name__ == 'OSNetwork':
            return self.net.requiredCheck()
        else:
            raise NotImplementedError

    def get_APs(self):
        if self.__class__.__name__ == 'OSNetwork':
            return self.net.get_APs()
        else:
            raise NotImplementedError

    def set_Adhoc(self, adhocName):
        if self.__class__.__name__ == 'OSNetwork':
            return self.net.set_Adhoc(adhocName)
        else:
            raise NotImplementedError

    def get_if(self):
        """return list of interfaces following:
            ['lo', 'en0', 'wlan0']"""
        if self.__class__.__name__ == 'OSNetwork':
            return self.net.get_if()
        else:
            raise NotImplementedError

    def connect_AP(self, ssid):
        if self.__class__.__name__ == 'OSNetwork':
            return self.net.connect_AP(ssid)
        else:
            raise NotImplementedError

def getOS():
    """Windows -> "Windows"
       MacOSX  -> "Darwin"
       Linux   -> "Linux"
    """
    return platform.system()

