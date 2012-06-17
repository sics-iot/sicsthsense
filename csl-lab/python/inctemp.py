import sicsthsense

d = sicsthsense.Device("csl-lab", "test-device")

x = d.get(['temperature'])
# x is a dict with name => value mapping and "temperature" is an
# array of temperature values, each being a dict of <timestamp>:<temp>
# so pick first element in "temp" array and pick first value (and only value)
# from the timestamed temp.
print "Response: ", x
t = x["temperature"][0].values()[0]
print "Temp: ", t
d.post(['temperature'], t + 1)
