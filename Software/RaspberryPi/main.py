# description: Main for measuring current ... tbd with ADC MCP3008
# author: Philipp Wuerfel
# date: 06.06.2019
# version: 0.1
# python_version : 3.5.3

from numpy import interp    # Map values
import time # set up delay
from mcp3008 import mcp3008
import json
import time
import sqlite3



# ------------------------------------------------------------------------------------
def read_current_acs712(spi_output):
    
    # current sensor
    #
    # Arduino
    # if no current at measuring circuit --> 2.5 V Output Signal --> 512
    # signal: -2.5V to + 2.5V --> ACS712 runs on 5 V Power Supply!

    # Raspberry Pi
    # if no current at measuring circuit --> 3.3 V / 2 Output Signal --> 512
    # ASS712 signal: -2.5V to + 2.5V --> ACS712 runs on 5 V Power Supply!
    # --> Add resistor to go from 5V to 3.3V max
    # U = R * I --> I_max = 30 A, U_max_arduino = 5 V /// Aim: new U_max_pi = 3.3V at 30 A
    # Resistor for 5 V to 3.3 V = 1.7V # double check!

    voltage_ref = 3.3
    voltage_zero_point = 1.650 #2500
    volt_per_ampere = 66 / 1000 # Millivolt per Ampere (100 for 20A module, 66 for 30A module)

    
    sensor_volt = (spi_output / 1024.0) * voltage_ref
    calc_load_current = ((sensor_volt - voltage_zero_point) / volt_per_ampere)
    return calc_load_current

# ------------------------------------------------------------------------------------
def read_voltage_sensor(spi_output):
    
    # voltage sensor
    # runs on 3.3 V Power Supply! Max Voltage: 16.5 V
    voltage_ref = 3.3 # reference voltage
    voltage_divide_factor = 5 # Spannungsteilungsfaktor 5:1
    
    sensor_voltage = (spi_output * voltage_ref)/1024.0
    calc_voltage = sensor_voltage * voltage_divide_factor

    return calc_voltage
# ------------------------------------------------------------------------------------

# ------------------------------------------------------------------------------------

# Open json File to save measured data (create if not available)
measure_json = open("MeasureData.json", "a")
# measure_json.close()

my_mcp = mcp3008(0,0)

# delay for reading
delay = 0.5

# Main Loop
while True:
    # Charging
    # current sensor ACS712
    spi_output = my_mcp.read(0) # Reading from CH0 --> 1   
    calc_charge_current = abs(read_current_acs712(spi_output))
    
    print('Channel 1 ::  ' + 'digital Value: ' + '{:4d}'.format(spi_output) + ' Charge Current : ' + '{:10.5f}'.format(calc_charge_current) + ' A')
    
    # -------------------------------------------------------------------------
    # Usage
    # current sensor ACS712
    spi_output = my_mcp.read(1) # Reading from CH1 --> 2    
    calc_load_current = abs(read_current_acs712(spi_output))*(-1)
    
    print('Channel 2 ::  ' + 'digital Value: ' + '{:4d}'.format(spi_output) + ' Load Current   : ' + '{:10.5f}'.format(calc_load_current) + ' A')
    
    # -------------------------------------------------------------------------
    # voltage sensor
    spi_output = my_mcp.read(2) # Reading from CH2 --> 3
    calc_voltage = read_voltage_sensor(spi_output)
    
    print('Channel 3 ::  ' + 'digital Value: ' + '{:4d}'.format(spi_output) + ' Battery Voltage: ' + '{:10.5f}'.format(calc_voltage) + ' V')
    
    print('-----------------------------------------------------------------------')
    power_usage = calc_voltage * calc_load_current
    print('Power Usage  ::  ' + '{0:.5f}'.format(power_usage) + ' W')
    power_charge = calc_voltage * calc_charge_current
    print('Power Charge ::  ' + '{0:.5f}'.format(power_charge) + ' W') 
    print('-----------------------------------------------------------------------')
    print('-----------------------------------------------------------------------')


    conn = sqlite3.connect('test.db')
    c = conn.cursor()
    c.execute("INSERT INTO Spannungen(currentOut, currentIn, currentBat, date) VALUES('" + str(power_usage) + "','" + str(power_charge) + "','" + str(calc_voltage) + "',datetime('now'))")
    conn.commit()
    conn.close()

    
    # new_data = {time.asctime():{"ChargeCurrent": 0.0, "LoadCurrent": calc_load_current, "BatteryVoltage": calc_voltage}}
    new_data = {"Timestamp": time.asctime(),"ChargeCurrent": 0.0, "LoadCurrent": calc_load_current, "BatteryVoltage": calc_voltage}
    
    '''
    # Open json File to save measured data (create if not available)
    try:
        with open("MeasureData.json", "r") as data_file:
            old_data = json.load(data_file)
            data_file.close()
    except Exception:
        old_data = {"Test":"123"}
        pass
    
    # Write Data to json file
    # new_data = {"Timestamp": time.asctime(),"ChargeCurrent": 0.0, "LoadCurrent": calc_load_current, "BatteryVoltage": calc_voltage}
    output = old_data
    output.update(new_data)
    # [old_data, new_data]# old_data.append(new_data)
    # data.append(new_data)
    # data = new_data
    with open("MeasureData.json", "w") as outfile:
        json.dump(output, outfile)
    '''
    
    json.dump(new_data, measure_json)
    
    time.sleep(delay)
    
my_mcp.close()


