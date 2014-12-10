#!/bin/bash
# debug script for turn on AP
ip link set $1 down
iwconfig $1 mode ad-hoc
iwconfig $1 essid $2
iwconfig $1 channel 4
iwconfig $1 freq 2.412G
iwconfig $1 key 73656e7368
ip link set $1 up
ifconfig $1 192.168.123.1 netmask 255.255.255.0

