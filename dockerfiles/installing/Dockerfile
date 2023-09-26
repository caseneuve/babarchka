# Container
FROM archlinux

# Install packages
RUN pacman -Syu --noconfirm --needed base-devel

# Add new user
ARG user=testuser
RUN useradd --create-home $user \
  && echo "$user ALL=(ALL:ALL) NOPASSWD:ALL" > /etc/sudoers.d/$user

# Run as new user
USER $user
WORKDIR /home/$user

# Copy the code to test
COPY dockerfiles/entrypoint.sh .
COPY pre/. .

# Run scripts
ENTRYPOINT ["./entrypoint.sh"]
