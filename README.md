# DeathTeleport

A Meteor Plus addon for Minecraft (2b2t.xin) that provides instant teleportation to your death location and automated easy kills (Auto Ez), plus spam protection.

&#x20;&#x20;

---

## 📦 Project Overview

**DeathTeleport** extends the Meteor Plus client with three powerful modules for the iconic 2b2t.xin server:

- **Infinite Death Respawn**: 死亡原地重生，用于组织合影干扰or刷头
- **Auto Ez**: 击杀播报，解决超级傻逼无敌Prism客户端无法自定义Auto Ez内容
- **Anti Spam**: 优雅绕过聊天限制
- **Nearby Player List**: 更优雅的附近玩家显示，死亡时，Nametag标红并停留并显示图腾爆炸数量，在Meteor ClickGUI上方点击GUI，点击Edit，右键，你可以在下方看到并使用
- **Infinite Death Respawn HUD**: 为死亡原地重生而写的HUD，主要功能是显示数据包发送的具体坐标
- **Explosion Phase**: 测试
- **Explosion Spoof**: 测试
- **Anti Explosion**: 测试

This addon is built to work seamlessly on 2b2t.xin, providing reliable performance even under strict server checks.

---

## ✨ Features

- ⚡ **Infinite Death Respawn**

  - Records death location and teleports you back instantly upon respawn.
  - Uses packet manipulation to avoid server-side checks.

- ⚔️ **Auto Ez**

  - Automatically sends configurable kill messages when you defeat a player.
  - Fully customizable messages via Meteor GUI.

- 🚫 **Anti Spam**

  - Bypass chat.

---

## 🛠 Installation

1. **Download the latest JAR** from the [Releases](#) page.
2. Copy the JAR into your `mods/.meteor-client/addons/` directory:
   ```bash
   ~/.minecraft/.meteor-client/addons/DeathTeleport.jar
   ```
3. Launch Minecraft with **Meteor Plus**.
4. Open the Meteor GUI and **enable** the `Infinite Death Respawn`, `Auto Ez`, and `Anti Spam` modules.

---

## ⚙️ Configuration

- Press `Right Shift` (default) to open the Meteor Plus GUI.
- Navigate to the **Addons** tab and select **DeathTeleport**.
- Expand each module to customize settings:
  - **Infinite Death Respawn**: Toggle packet bypass.
  - **Auto Ez**: Edit message templates.
  - **Anti Spam**: Define spam keywords and thresholds.

---

## 🎮 Usage

1. Join **2b2t.xin**.
2. Enable the modules in the Meteor GUI.
3. Enjoy instant teleportation back to your death point and automated PvP messages.

---

## 📜 Changelog

### 2025/06/28

- Updated default **Auto Ez** messages.
- Added **Anti Spam** module.
- Minor bug fixes and performance improvements.

---

## 📚 Requirements

- **Meteor Plus** client (tested on v1.20.4)
- Minecraft 1.20.4

---

## 🤝 Contributing

Feel free to open issues or submit pull requests on the [GitHub repository](#).

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).

---

## 📬 Contact

- Author: @yourname (GitHub)
- Server: 2b2t.xin
- Email: [yourname@example.com](mailto\:yourname@example.com)

