import { createContext, useContext, useEffect, useState } from "react";
import { useSelector } from "react-redux";
import { io } from "socket.io-client";

const SocketContext = createContext();

export const useSocketContext = () => {
  return useContext(SocketContext);
};

const SOCKET_URL = import.meta.env.VITE_SOCKET_URL;

export const SocketContextProvider = ({ children }) => {
  const [socket, setSocket] = useState(null);
  const [onlineUsers, setOnlineUsers] = useState([]);

  const { userData } = useSelector((state) => state.auth);

  useEffect(() => {
    if (userData && userData.id) {
      const newSocket = io(SOCKET_URL, {
        transports: ['polling', 'websocket'],
        withCredentials: true,

        reconnection: true,
        reconnectionAttempts: 5,

        query: {
            userId: userData.id
        }
      });

      setSocket(newSocket);

      newSocket.on("connect", () => {
        console.log("Global Socket Connected:", newSocket.id);
        newSocket.emit("newUser", userData.id);
      });

      newSocket.on("getOnlineUsers", (users) => {
        setOnlineUsers(users);
      });

      return () => {
        newSocket.close();
        setSocket(null);
      };
    } else {
      if (socket) {
        socket.close();
        setSocket(null);
      }
    }
  }, [userData]);
  return (
    <SocketContext.Provider value={{ socket, onlineUsers }}>
      {children}
    </SocketContext.Provider>
  );
};