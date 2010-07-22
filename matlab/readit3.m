function [] = readit3(fname)
fid = fopen(fname,'r');
av = zeros(6,50);
stddev = zeros(6,50);
fid = fopen(fname,'r');
for (i=1:6),
hdr = fscanf(fid,'%s ',3);
vals = fscanf(fid,'%f%f%f',[3 50]);
av(i,:) = vals(2,:);
stddev(i,:) = vals(3,:);
end
fclose(fid);
frac = vals(1,:);
figure(1);clf;
errorbar(frac,av(2,:),stddev(2,:),'kx');
hold on;
errorbar(frac,av(3,:),stddev(3,:),'bo');
hold on;
errorbar(frac,av(4,:),stddev(4,:),'r*');
hold on;
errorbar(frac,av(5,:),stddev(5,:),'gv');
hold on;
errorbar(frac,av(6,:),stddev(6,:),'m.');
plot(frac,av(2,:),'k',frac,av(3,:),'b',frac,av(4,:),'r',frac,av(5,:),'g',frac,av(6,:),'m');

axis([0 0.5 0 1]);

