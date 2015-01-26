
# format:
# "fromNode(:relayNode)::distNode:msg"
# fromNode = prefix("senshin") + "_" + macaddress(ex. ABCDEF123456)
# senshin_ABCDEF123456

def parser(_msg):
    srcNode = ""
    distNode = ""
    relayNode = []
    msg = ""
    chkflag = True

    srcNode_end = _msg.find(":")
    srcNode = _msg[:srcNode_end]
    
    if not chkNode(srcNode): # excpected MAC address
        chkflag = False
        msg += "invalid format for srcNode: %s, "%(srcNode)
    

    distNode_begin = _msg.find("::")+2
    distNode_end = _msg.find(":", distNode_begin+2)
    distNode = _msg[distNode_begin:distNode_end]

    if not chkNode(distNode):
        chkflag = False
        msg += "invalid format for distNode: %s, "%(distNode)


    relayNode = _msg[srcNode_end+1:distNode_begin-2].split(":")

    if len(relayNode) == 1 and relayNode[0] == '':
        relayNode = []

    for node in relayNode:
        if not chkNode(node):
            chkflag = False
            msg += "invalid format for relayNode: %s "%(node)

    if chkflag:
        msg = _msg[distNode_end+1:]

    return (chkflag, srcNode, distNode, relayNode, msg)

def chkNode(node):
    return chkMAC(node[node.rfind("_")+1:])

def chkMAC(addr):
    vaild_char = "0123456789ABCDEFabcdef"

    if len(addr) != 12:
        return False

    for c in addr:
        if vaild_char.find(c) == -1:
            return False

    return True
