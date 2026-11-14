package com.findaries.horror.world;

import com.findaries.horror.FindariesHorror;
import com.findaries.horror.entity.HorrorEntities;
import com.findaries.horror.entity.LongFigureEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Vector3f;
import java.util.*;

@Mod.EventBusSubscriber(modid = FindariesHorror.MOD_ID)
public final class HorrorWorldEvents {
    private static final Random RANDOM = new Random();
    private static final Map<UUID, Integer> TIMERS = new HashMap<>();
    private static final Map<UUID, Integer> DIMENSION_TIME = new HashMap<>();
    private static int worldClock;
    private static final List<Scheduled> TASKS = new ArrayList<>();

    @SubscribeEvent public static void serverTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        MinecraftServer server = event.getServer();
        worldClock++;
        Iterator<Scheduled> taskIterator = TASKS.iterator();
        while (taskIterator.hasNext()) { Scheduled task = taskIterator.next(); if (task.at <= worldClock) { taskIterator.remove(); task.action.run(); } }
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            UUID id = player.getUUID();
            int remaining = TIMERS.getOrDefault(id, 6000 + RANDOM.nextInt(6000)) - 1;
            if (remaining <= 0) { triggerRandomEvent(player); remaining = 3600 + RANDOM.nextInt(9000); }
            TIMERS.put(id, remaining);
            String area = areaName(player.serverLevel(), player.blockPosition());
            if (area.equals("wonderflowers")) {
                int t = DIMENSION_TIME.merge(id, 1, Integer::sum);
                if (t > 400 && RANDOM.nextInt(1800) == 0) spawnDanger(player);
            } else if (area.equals("wonderland")) {
                int t = DIMENSION_TIME.merge(id, 1, Integer::sum);
                if (t > 600 && RANDOM.nextInt(1200) == 0) spawnWatcher(player);
            } else DIMENSION_TIME.remove(id);
        }
        if (worldClock % 200 == 0) TIMERS.keySet().removeIf(id -> server.getPlayerList().getPlayer(id) == null);
    }

    private static void triggerRandomEvent(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        String area = areaName(level, player.blockPosition());
        if (!area.equals("overworld") && !area.equals("wonderland")) return;
        switch (RANDOM.nextInt(9)) {
            case 0 -> skyTriangle(player);
            case 1 -> mushroomRing(player);
            case 2 -> cobblePillar(player);
            case 3 -> spawnLongFigure(player);
            case 4 -> spawnBlueWalker(player);
            case 5 -> skySlab(player);
            case 6 -> skyUfo(player);
            case 7 -> woodenPortalHouse(player);
            default -> spawnPortal(player);
        }
    }

    private static void skyTriangle(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        BlockPos center = player.blockPosition().above(24 + RANDOM.nextInt(10));
        level.playSound(null, player.blockPosition(), SoundEvents.WARDEN_ROAR, SoundSource.AMBIENT, 2.0F, 0.55F);
        // A huge, moving, three-dimensional silhouette made from hundreds of dark ash motes.
        for (int tick = 0; tick < 90; tick += 9) {
            final int frame = tick;
            TASKS.add(new Scheduled(worldClock + tick, () -> {
                if (!player.isAlive() || player.serverLevel() != level) return;
                double cx = center.getX() + 42 - frame * 0.95;
                double cy = center.getY() + Math.sin(frame * .08) * 3;
                double cz = center.getZ() + 4;
                double[][] p = {{cx,cy,cz-17},{cx-26,cy-7,cz+19},{cx+26,cy-7,cz+19}};
                for (int edge=0; edge<3; edge++) {
                    double[] a=p[edge], b=p[(edge+1)%3];
                    for (int n=0; n<=18; n++) {
                        double q=n/18.0;
                        level.sendParticles(new DustParticleOptions(new Vector3f(.015f,.018f,.025f), 2.5f), a[0]+(b[0]-a[0])*q, a[1]+(b[1]-a[1])*q, a[2]+(b[2]-a[2])*q, 2, .15,.15,.15,0);
                    }
                }
                // cross-bracing gives the passing shape depth instead of a flat outline
                for (int n=0;n<24;n+=3) {
                    double q=n/24.0;
                    level.sendParticles(ParticleTypes.LARGE_SMOKE, cx-16+q*32,cy-4,cz+8,2,.4,.25,.4,0);
                }
            }));
        }
    }


    private static void skySlab(ServerPlayer player) {
        ServerLevel level=player.serverLevel(); BlockPos pos=player.blockPosition().above(25);
        level.playSound(null,player.blockPosition(),SoundEvents.WITHER_AMBIENT,SoundSource.AMBIENT,.7F,.45F);
        for(int frame=0;frame<70;frame+=4){final int f=frame; TASKS.add(new Scheduled(worldClock+frame,()->{
            if(!player.isAlive()||player.serverLevel()!=level)return;
            double cx=pos.getX()+34-f, cy=pos.getY()+Math.sin(f*.07)*2, cz=pos.getZ()+2;
            for(int x=-9;x<=9;x+=3)for(int z=-4;z<=4;z+=2){
                if(Math.abs(x)<9||Math.abs(z)<3)level.sendParticles(new DustParticleOptions(new Vector3f(.008f,.009f,.014f),2.0f),cx+x,cy+z*.22,cz+z,1,0,0,0,0);
            }
            for(int n=0;n<5;n++)level.sendParticles(ParticleTypes.SMOKE,cx-12+RANDOM.nextInt(24),cy-2,cz-5+RANDOM.nextInt(10),2,.1,.1,.1,0);
        }));}
    }
    private static void skyUfo(ServerPlayer player) {
        ServerLevel level=player.serverLevel(); BlockPos pos=player.blockPosition().above(28);
        level.playSound(null,player.blockPosition(),SoundEvents.WARDEN_ROAR,SoundSource.AMBIENT,1.3F,.38F);
        // A colossal saucer crosses the sky. Its broad, flattened rings create the silhouette and perspective.
        for(int frame=0;frame<100;frame+=5){final int f=frame; TASKS.add(new Scheduled(worldClock+frame,()->{
            if(!player.isAlive()||player.serverLevel()!=level)return;
            double cx=pos.getX()-450+f*9, cy=pos.getY()+Math.sin(f*.04)*8, cz=pos.getZ()+3;
            for(int n=0;n<192;n+=4){
                double a=2*Math.PI*n/192.0; double r=475+28*Math.sin(a*3);
                double xx=cx+Math.cos(a)*r, zz=cz+Math.sin(a)*r*.48;
                level.sendParticles(new DustParticleOptions(new Vector3f(.004f,.006f,.012f),3.4f),xx,cy+Math.sin(a*2)*3,zz,2,.8,.3,.8,0);
                if(n%3==0)level.sendParticles(ParticleTypes.SMOKE,xx,cy-5,zz,1,.8,.2,.8,0);
            }
            for(int n=0;n<18;n+=3){double a=n*Math.PI/9;level.sendParticles(new DustParticleOptions(new Vector3f(.009f,.012f,.022f),2.5f),cx+Math.cos(a)*24,cy+4,cz+Math.sin(a)*12,1,.2,.2,.2,0);}
        }));}
    }

    private static void mushroomRing(ServerPlayer player) {
        ServerLevel level=player.serverLevel(); BlockPos origin=player.blockPosition();
        level.playSound(null, origin, SoundEvents.AMBIENT_CAVE.value(), SoundSource.AMBIENT, 1.7F, .65F);
        List<BlockPos> placed=new ArrayList<>();
        List<BlockPos> giantPlaced=new ArrayList<>();
        for (int i=0;i<2;i++) {
            BlockPos giant=origin.offset((i==0?-1:1)*8,0,(i==0?3:-4));
            int gy=level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,giant.getX(),giant.getZ());
            giant=new BlockPos(giant.getX(),gy,giant.getZ());
            int sh=7+RANDOM.nextInt(4);
            for(int h=0;h<sh;h++){BlockPos q=giant.above(h);if(level.getBlockState(q).isAir()){level.setBlock(q,Blocks.MUSHROOM_STEM.defaultBlockState(),3);giantPlaced.add(q);}}
            for(int dx=-2;dx<=2;dx++)for(int dz=-2;dz<=2;dz++)if(Math.abs(dx)+Math.abs(dz)<4){BlockPos q=giant.above(sh).offset(dx,0,dz);if(level.getBlockState(q).isAir()){level.setBlock(q,Blocks.RED_MUSHROOM_BLOCK.defaultBlockState(),3);giantPlaced.add(q);}}
        }
        for (int i=0;i<14;i++) {
            double a=i*Math.PI*2/14; BlockPos p=origin.offset((int)(Math.cos(a)*5),0,(int)(Math.sin(a)*5));
            int y=level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,p.getX(),p.getZ()); p=new BlockPos(p.getX(),y,p.getZ());
            if (level.getBlockState(p.below()).isSolid() && level.getBlockState(p).isAir()) { level.setBlock(p, i%2==0?Blocks.RED_MUSHROOM.defaultBlockState():Blocks.BROWN_MUSHROOM.defaultBlockState(),3); placed.add(p); }
        }
        for (int n=0;n<18;n++) level.sendParticles(ParticleTypes.SPORE_BLOSSOM_AIR, origin.getX()+RANDOM.nextGaussian()*5, origin.getY()+1, origin.getZ()+RANDOM.nextGaussian()*5, 8,.1,.4,.1,.02);
        TASKS.add(new Scheduled(worldClock+2400,()->{for(BlockPos b:placed) if(level.getBlockState(b).is(Blocks.RED_MUSHROOM)||level.getBlockState(b).is(Blocks.BROWN_MUSHROOM)) level.removeBlock(b,false);}));
        TASKS.add(new Scheduled(worldClock+320,()->{for(BlockPos b:giantPlaced) if(level.getBlockState(b).is(Blocks.MUSHROOM_STEM)||level.getBlockState(b).is(Blocks.RED_MUSHROOM_BLOCK)) level.removeBlock(b,false);}));
    }

    private static void cobblePillar(ServerPlayer player) {
        ServerLevel level=player.serverLevel(); BlockPos p=player.blockPosition().offset(7,0,4);
        int y=level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,p.getX(),p.getZ());
        p=new BlockPos(p.getX(),y,p.getZ());
        final BlockPos base=p;
        for(int i=0;i<50;i++){ final int h=i; TASKS.add(new Scheduled(worldClock+i*2,()->{
            level.setBlock(base.above(h),Blocks.COBBLESTONE.defaultBlockState(),3);
            double yy=base.getY()+h+2.0;
            for(int n=0;n<7;n++){double a=worldClock*.16+n*Math.PI*2/7;level.sendParticles(new DustParticleOptions(new Vector3f(1.0f,.88f,.05f),1.7f),base.getX()+.5+Math.cos(a)*2,yy,base.getZ()+.5+Math.sin(a)*2,2,.15,.15,.15,0);}
        }));}
        level.playSound(null,p,SoundEvents.STONE_BREAK,SoundSource.AMBIENT,1.2F,.6F);
        level.sendParticles(ParticleTypes.CRIT,p.getX()+.5,p.getY()+12,p.getZ()+.5,65,1,8,1,.1);
    }

    private static void woodenPortalHouse(ServerPlayer player) {
        ServerLevel level=player.serverLevel(); BlockPos p=findGround(level,player.blockPosition().offset(11,0,6));
        for(int dx=0;dx<7;dx++)for(int dz=0;dz<7;dz++)for(int h=0;h<5;h++){
            boolean wall=dx==0||dx==6||dz==0||dz==6||h==0||h==4;
            BlockPos q=p.offset(dx,h,dz); if(wall)level.setBlock(q,Blocks.OAK_PLANKS.defaultBlockState(),3);
        }
        for(int h=1;h<=2;h++)level.setBlock(p.offset(3,h,0),Blocks.AIR.defaultBlockState(),3);
        for(int dx=0;dx<7;dx++)level.setBlock(p.offset(dx,5,3),Blocks.SPRUCE_PLANKS.defaultBlockState(),3);
        placePortal(level,p.offset(3,1,3));
        level.playSound(null,p,SoundEvents.WOODEN_DOOR_CLOSE,SoundSource.AMBIENT,1.3F,.65F);
        player.displayClientMessage(Component.literal("В лесу появился старый деревянный дом..."),true);
    }

    private static void spawnLongFigure(ServerPlayer player) {
        ServerLevel level=player.serverLevel(); BlockPos p=findGround(level,player.blockPosition().offset(10+RANDOM.nextInt(12),0,7));
        LongFigureEntity mob=HorrorEntities.LONG_FIGURE.get().create(level); if(mob==null)return;
        mob.moveTo(p.getX()+.5,p.getY(),p.getZ()+.5,level.random.nextFloat()*360,0); level.addFreshEntity(mob);
        level.playSound(null,p,SoundEvents.WARDEN_AMBIENT,SoundSource.AMBIENT,2,.7F);
        mob.getPersistentData().putBoolean("fd_horror",true);
    }
    private static void spawnDanger(ServerPlayer player) {
        ServerLevel level=player.serverLevel(); LongFigureEntity mob=HorrorEntities.DANGER_12.get().create(level); if(mob==null)return;
        BlockPos p=findGround(level,player.blockPosition().offset(22,0,18)); mob.moveTo(p.getX()+.5,p.getY(),p.getZ()+.5,0,0); level.addFreshEntity(mob);
        level.playSound(null,p,SoundEvents.WARDEN_HEARTBEAT,SoundSource.AMBIENT,1.5F,.7F);
    }
    private static void spawnWatcher(ServerPlayer player) {
        ServerLevel level=player.serverLevel(); LongFigureEntity mob=HorrorEntities.WONDER_WATCHER.get().create(level); if(mob==null)return;
        BlockPos p=findGround(level,player.blockPosition().offset(-18-RANDOM.nextInt(12),0,12)); mob.moveTo(p.getX()+.5,p.getY(),p.getZ()+.5,0,0); mob.setNoAi(true); level.addFreshEntity(mob);
        level.playSound(null,p,SoundEvents.AMBIENT_CAVE.value(),SoundSource.AMBIENT,.8F,.8F);
    }
    private static void spawnBlueWalker(ServerPlayer player) {
        ServerLevel level=player.serverLevel(); BlockPos p=findGround(level,player.blockPosition().offset(10+RANDOM.nextInt(8),0,-8));
        LongFigureEntity mob=HorrorEntities.BLUE_WALKER.get().create(level); if(mob==null)return;
        mob.moveTo(p.getX()+.5,p.getY(),p.getZ()+.5,0,0); mob.setNoAi(true); level.addFreshEntity(mob);
        mob.getPersistentData().putBoolean("fd_blue",true);
        level.playSound(null,p,SoundEvents.PLAYER_SPLASH_HIGH_SPEED,SoundSource.AMBIENT,1.5F,.55F);
    }

    private static void spawnPortal(ServerPlayer player) {
        ServerLevel level=player.serverLevel(); BlockPos p=findGround(level,player.blockPosition().offset(9+RANDOM.nextInt(10),0,8));
        if (level.getBlockState(p).isAir() && level.getBlockState(p.below()).isSolid()) {
            level.setBlock(p,FindariesHorror.WONDER_PORTAL.get().defaultBlockState(),3);
            level.sendParticles(ParticleTypes.PORTAL,p.getX()+.5,p.getY()+1,p.getZ()+.5,60,.5,1,.5,.4);
            level.playSound(null,p,SoundEvents.END_PORTAL_SPAWN,SoundSource.AMBIENT,1,.7F);
        }
    }

    public static void enterPortal(ServerPlayer player, BlockPos portalPos) {
        ServerLevel level=player.serverLevel();
        String area=areaName(level,portalPos);
        String target=area.equals("overworld")?"wonderland":area.equals("wonderland")?"wonderflowers":area.equals("wonderflowers")?"cage_wonderland":"overworld";
        BlockPos at;
        if(target.equals("overworld")) {
            at=level.getSharedSpawnPos();
            if(at.getY()<2) at=new BlockPos(0,level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,0,0),0);
        } else {
            int center=areaCenter(target);
            at=new BlockPos(center,level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,center,0),0);
            prepareDimension(level,target,at);
        }
        player.connection.teleport(at.getX()+.5,at.getY(),at.getZ()+.5,player.getYRot(),player.getXRot());
        if(target.equals("wonderland")) player.displayClientMessage(Component.literal("minecraft.1.wonderland.0"),true);
        if(target.equals("overworld")&&area.equals("cage_wonderland")) {
            var advancement=player.getServer().getAdvancements().getAdvancement(FindariesHorror.id("alive"));
            if(advancement!=null) player.getAdvancements().award(advancement,"escape");
        }
        if(target.equals("wonderflowers")){for(int i=0;i<5;i++){final int step=i;TASKS.add(new Scheduled(worldClock+i*3,()->level.playSound(null,portalPos,SoundEvents.WARDEN_STEP,SoundSource.AMBIENT,1.2F,.65F+step*.08F)));}}
        level.playSound(null,at,SoundEvents.AMBIENT_CAVE.value(),SoundSource.AMBIENT,.8F,.7F);
    }

    private static final Set<String> BUILT=Collections.synchronizedSet(new HashSet<>());
    private static void prepareDimension(ServerLevel level,String name,BlockPos spawn) {
        if (!BUILT.add(name)) return;
        int x=spawn.getX(),y=Math.max(spawn.getY(),4),z=spawn.getZ();
        for(int dx=-16;dx<=16;dx++)for(int dz=-16;dz<=16;dz++) level.setBlock(new BlockPos(x+dx,y-1,z+dz),name.equals("cage_wonderland")?Blocks.OAK_PLANKS.defaultBlockState():Blocks.GRASS_BLOCK.defaultBlockState(),3);
        if(name.equals("wonderland")) {
            for(int i=0;i<8;i++) giantFlower(level,new BlockPos(x-13+i*4,y,z+4+(i%2)*5),i%2==0);
            for(int i=0;i<7;i++) twistedTree(level,new BlockPos(x-15+i*5,y,z+12),i);
            buildWonderHouse(level,new BlockPos(x-4,y,z-10));
            buildElevatorHouse(level,new BlockPos(x+8,y,z-4));
        } else if(name.equals("wonderflowers")) {
            for(int i=0;i<13;i++) giantFlower(level,new BlockPos(x-12+i*2,y,z+6),true);
            for(int dx=0;dx<=9;dx++)for(int dz=-1;dz<=1;dz++)level.setBlock(new BlockPos(x+dx,y-1,z+dz),Blocks.COARSE_DIRT.defaultBlockState(),3);
            for(int i=0;i<5;i++)giantFlower(level,new BlockPos(x-5+i*5,y,z-8-(i%2)*3),i%2==0);
            for(int dz=-3;dz<=3;dz++) for(int dx=10;dx<=18;dx++) level.setBlock(new BlockPos(x+dx,y-1,z+dz),Blocks.WATER.defaultBlockState(),3);
            for(int dx=10;dx<=17;dx++) level.setBlock(new BlockPos(x+dx,y,z),Blocks.OAK_PLANKS.defaultBlockState(),3);
            for(int dx=19;dx<=23;dx++)for(int dz=-3;dz<=3;dz++)level.setBlock(new BlockPos(x+dx,y-1,z+dz),Blocks.GRASS_BLOCK.defaultBlockState(),3);
            for(int i=-1;i<=1;i++) for(int j=-1;j<=1;j++) level.setBlock(new BlockPos(x+20+i,y,z+j),FindariesHorror.WONDER_PORTAL.get().defaultBlockState(),3);
            level.sendParticles(ParticleTypes.PORTAL,x+20.5,y+1,z+.5,180,1,1,1,.8);
        } else if(name.equals("cage_wonderland")) {
            for(int dx=-16;dx<=16;dx++){level.setBlock(new BlockPos(x+dx,y+5,z-14),Blocks.OAK_PLANKS.defaultBlockState(),3);level.setBlock(new BlockPos(x+dx,y+5,z+14),Blocks.OAK_PLANKS.defaultBlockState(),3);}
            for(int dz=-14;dz<=14;dz++){level.setBlock(new BlockPos(x-16,y+5,z+dz),Blocks.OAK_PLANKS.defaultBlockState(),3);level.setBlock(new BlockPos(x+16,y+5,z+dz),Blocks.OAK_PLANKS.defaultBlockState(),3);}
            for(int i=0;i<8;i++){int tx=x-12+i*3;int treeHeight=28+i*4;for(int h=0;h<treeHeight;h++)level.setBlock(new BlockPos(tx,y+h,z-7),Blocks.OAK_LOG.defaultBlockState(),3);for(int dx=-3;dx<=3;dx++)for(int dz=-3;dz<=3;dz++)if(Math.abs(dx)+Math.abs(dz)<6)level.setBlock(new BlockPos(tx+dx,y+treeHeight-1,z-7+dz),Blocks.OAK_LEAVES.defaultBlockState(),3);}
            for(int h=0;h<4;h++)for(int dx=-1;dx<=1;dx++)level.setBlock(new BlockPos(x+dx,y+h,z-14),Blocks.AIR.defaultBlockState(),3);
            level.setBlock(new BlockPos(x,y+1,z-14),FindariesHorror.WONDER_PORTAL.get().defaultBlockState(),3);
        } else placePortal(level,new BlockPos(x,y,z));
    }
    private static void twistedTree(ServerLevel level,BlockPos p,int seed) {
        int height=9+(seed%4)*2;
        for(int h=0;h<height;h++)level.setBlock(p.above(h),Blocks.OAK_LOG.defaultBlockState(),3);
        for(int branch=0;branch<3;branch++){
            int direction=(branch%2==0?1:-1), start=height/2+branch;
            for(int step=1;step<=4;step++){
                BlockPos limb=p.offset(direction*step,start+step/2,(branch-1)*step/2);
                level.setBlock(limb,Blocks.OAK_LOG.defaultBlockState(),3);
                for(int dx=-2;dx<=2;dx++)for(int dz=-2;dz<=2;dz++)if(Math.abs(dx)+Math.abs(dz)<4)level.setBlock(limb.above(1).offset(dx,0,dz),Blocks.OAK_LEAVES.defaultBlockState(),3);
            }
        }
        for(int dx=-3;dx<=3;dx++)for(int dz=-3;dz<=3;dz++)if(Math.abs(dx)+Math.abs(dz)<6)level.setBlock(p.above(height).offset(dx,0,dz),Blocks.OAK_LEAVES.defaultBlockState(),3);
    }
    private static void giantFlower(ServerLevel level,BlockPos p,boolean poppy) {
        int height=8+RANDOM.nextInt(9); for(int h=0;h<height;h++)level.setBlock(p.above(h),Blocks.OAK_LOG.defaultBlockState(),3);
        BlockPos top=p.above(height);
        BlockState petal=(poppy?Blocks.RED_WOOL:Blocks.YELLOW_WOOL).defaultBlockState();
        level.setBlock(top,Blocks.YELLOW_WOOL.defaultBlockState(),3);
        for(int dx=-2;dx<=2;dx++) for(int dz=-2;dz<=2;dz++) if(Math.abs(dx)+Math.abs(dz)<4 && (dx!=0||dz!=0)) level.setBlock(top.offset(dx,0,dz),petal,3);
    }
    private static void buildElevatorHouse(ServerLevel level,BlockPos p) {
        for(int dx=0;dx<7;dx++)for(int dz=0;dz<7;dz++)for(int h=0;h<5;h++){boolean wall=dx==0||dx==6||dz==0||dz==6||h==0||h==4; if(wall)level.setBlock(p.offset(dx,h,dz),Blocks.SPRUCE_PLANKS.defaultBlockState(),3);}
        for(int h=1;h<=2;h++)level.setBlock(p.offset(3,h,0),Blocks.AIR.defaultBlockState(),3);
        for(int h=1;h<5;h++)level.setBlock(p.offset(3,-h,3),Blocks.OAK_PLANKS.defaultBlockState(),3);
        level.setBlock(p.offset(1,1,1),Blocks.BOOKSHELF.defaultBlockState(),3);
        level.setBlock(p.offset(5,1,1),Blocks.FURNACE.defaultBlockState(),3);
        level.setBlock(p.offset(1,1,5),Blocks.CRAFTING_TABLE.defaultBlockState(),3);
        level.setBlock(p.offset(5,1,5),Blocks.RED_BED.defaultBlockState(),3);
        level.setBlock(p.offset(5,2,4),Blocks.LANTERN.defaultBlockState(),3);
        placePortal(level,p.offset(3,1,3));
    }
    private static void buildWonderHouse(ServerLevel level,BlockPos p) {
        for(int dx=0;dx<8;dx++)for(int dz=0;dz<7;dz++)for(int h=0;h<5;h++) {
            boolean wall=dx==0||dx==7||dz==0||dz==6||h==0||h==4;
            BlockState material=h==4?Blocks.CRAFTING_TABLE.defaultBlockState():((dx==0||dx==7||dz==0||dz==6)?Blocks.DIAMOND_BLOCK.defaultBlockState():Blocks.COBBLESTONE.defaultBlockState());
            level.setBlock(p.offset(dx,h,dz),wall?material:Blocks.AIR.defaultBlockState(),3);
        }
        level.setBlock(p.offset(0,1,3),Blocks.AIR.defaultBlockState(),3);level.setBlock(p.offset(0,2,3),Blocks.AIR.defaultBlockState(),3);
        BlockPos chest=p.offset(4,1,3);level.setBlock(chest,Blocks.CHEST.defaultBlockState(),3);
        BlockEntity be=level.getBlockEntity(chest);
        if(be instanceof ChestBlockEntity c){
            ItemStack note=new ItemStack(Items.WRITTEN_BOOK);
            var tag=note.getOrCreateTag();
            tag.putString("title","Wonderland"); tag.putString("author","???");
            net.minecraft.nbt.ListTag pages=new net.minecraft.nbt.ListTag();
            pages.add(net.minecraft.nbt.StringTag.valueOf(Component.Serializer.toJson(Component.literal("fun fun fun fun fun fun wonderland is fun place fun fun"))));
            tag.put("pages",pages); c.setItem(0,note);
        }
    }
    private static void placePortal(ServerLevel level,BlockPos p){for(int dx=-1;dx<=1;dx++)for(int dy=0;dy<3;dy++)level.setBlock(p.offset(dx,dy,0),FindariesHorror.WONDER_PORTAL.get().defaultBlockState(),3);}
    private static BlockPos findGround(ServerLevel level,BlockPos p){int y=level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,p.getX(),p.getZ());return new BlockPos(p.getX(),y,p.getZ());}
    private static String areaName(ServerLevel level,BlockPos pos) {
        if(level.dimension()!=Level.OVERWORLD) return "overworld";
        for(String name:List.of("wonderland","wonderflowers","cage_wonderland")) if(Math.abs(pos.getX()-areaCenter(name))<384&&Math.abs(pos.getZ())<384) return name;
        return "overworld";
    }
    private static int areaCenter(String name) {
        return switch(name){case "wonderland"->30000;case "wonderflowers"->60000;case "cage_wonderland"->90000;default->0;};
    }

    @SubscribeEvent public static void onStopping(ServerStoppingEvent event) {
        TASKS.clear(); TIMERS.clear(); DIMENSION_TIME.clear(); BUILT.clear(); worldClock=0;
    }

    @SubscribeEvent public static void entityTick(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof LongFigureEntity mob) || mob.level().isClientSide) return;
        ServerLevel level=(ServerLevel)mob.level();
        if(mob.getType()==HorrorEntities.WONDER_WATCHER.get()) {
            for(ServerPlayer p:level.players()) if(p.distanceToSqr(mob)<100) {BlockPos at=mob.blockPosition();mob.discard();if(level.getBlockState(at).isAir()&&level.getBlockState(at.below()).isSolid())level.setBlock(at,Blocks.DANDELION.defaultBlockState(),3);level.sendParticles(ParticleTypes.POOF,at.getX()+.5,at.getY()+1,at.getZ()+.5,24,.4,.6,.4,.03);break;}
        } else if(mob.getType()==HorrorEntities.BLUE_WALKER.get()) {
            for(ServerPlayer p:level.players()) if(p.distanceToSqr(mob)<7) {BlockPos at=mob.blockPosition(); mob.discard(); if(level.getBlockState(at).isAir())level.setBlock(at,Blocks.WATER.defaultBlockState(),3);level.playSound(null,at,SoundEvents.PLAYER_SPLASH,SoundSource.AMBIENT,1,.7F);break;}
        } else {
            for(ServerPlayer p:level.players()) if(p.distanceToSqr(mob)<2.0) {p.connection.disconnect(Component.literal("Он заметил тебя."));mob.discard();break;}
        }
    }
    private record Scheduled(int at, Runnable action) {}
    private HorrorWorldEvents(){}
}
