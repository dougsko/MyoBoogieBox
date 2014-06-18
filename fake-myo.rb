require 'socket'      # Sockets are in standard library

hostname = 'localhost'
port = 6969

@s = TCPSocket.open(hostname, port)

def send(cmd)
    if cmd.match(/1/)
        @s.puts "{\"type\":\"diff\",\"x\":" + rand(255).to_s + ", \"y\":" + rand(255).to_s + ", \"z\":" + rand(255).to_s + "}"
    elsif cmd.match(/2/)
        @s.puts "{\"type\":\"control\",\"command\":\"allOff\"}"
	elsif cmd.match(/3/)
		@s.puts "{\"type\":\"control\",\"command\":\"channelUp\"}"
    elsif cmd.match(/4/)
        @s.close
        exit 0
    end
end

loop do
    puts "enter command:"
    puts "1 to send fist"
    puts "2 to send all-off"
	puts "3 to send channelUp"
    puts "4 to quit"
    cmd = gets.chomp + "\r\n"
    puts cmd
    send(cmd)
end



@s.close   