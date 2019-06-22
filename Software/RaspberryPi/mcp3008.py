# description: Reading analog values with ADC MCP3008
# author: Philipp Wuerfel
# date: 28.05.2019
# version: 0.1
# python_version : 3.5.3

import spidev # SPI Interface
 
class mcp3008:
    def __init__(self, bus = 0, device = 0, max_speed_hz = 1350000):
                       
        self.bus = bus
        self.device = device
        self.spi = spidev.SpiDev()
        self.spi.open(self.bus, self.device)
        self.spi.max_speed_hz = max_speed_hz   
           
    def read(self, channel):
        # check if valid channelnumber --> MCP3008 has 8 channels
        if channel > 7 or channel < 0:
            return -1              
        adc = self.spi.xfer2([1,(8+channel)<<4,0])
        data = ((adc[1]&3) << 8) + adc[2]
        return data
      
    def close(self):
        self.spi.close()  