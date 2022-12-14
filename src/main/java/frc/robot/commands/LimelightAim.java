package frc.robot.commands;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.LimeLightSubsystem;


public class LimelightAim extends CommandBase {

    private static final double kPangle = 1.65;
    private static final double kIangle = 0.005;
    private static final double kDangle = 0;
    private static final double timeDiff = 0.02;
    
    private static final double kPdist = 0.4;
    private static final double kIdist = 0;
    private static final double kDdist = 0;

    private double target;

    private double distError;
    private double integralSumDist;
    private double lastDistError;

    private double xError;
    private double integralSumX;
    private double lastError;

    private DriveSubsystem m_drive;
    private LimeLightSubsystem m_limelight;

    public LimelightAim(DriveSubsystem driveSubsystem, LimeLightSubsystem limelight) {
        m_drive = driveSubsystem;
        m_limelight = limelight;
        addRequirements(driveSubsystem);
        addRequirements(limelight);
    }

    public void initialize() {
        xError = m_limelight.getX()*Math.PI/180;
        distError = m_limelight.getDis();
        System.out.println("Executing Limelight Aim");
    }

    public void execute() {
        target = m_limelight.getTarget();

        xError = m_limelight.getX()*Math.PI/180;
        distError = m_limelight.getDis();
        if (Math.abs(integralSumX) < 100) {
            integralSumX += xError;
        }
        if (Math.abs(integralSumDist) < 1000){
            integralSumDist += distError;
        }

        double distDerivative = (distError - lastDistError) / timeDiff;
        double derivative = (xError - lastError) / timeDiff;

        double output = kPangle * xError + kIangle * integralSumX + kDangle * derivative;
        double distOutput = kPdist * distError + kIdist * integralSumDist + kDdist * distDerivative;
        
        if (target>0 && distError>0){
            m_drive.arcadeDrive(distOutput, output);
        } else {
            m_drive.arcadeDrive(0, 0);
        }
        
        lastError = xError;
        lastDistError = distError;
        SmartDashboard.putNumber("distOutput",distOutput);
        SmartDashboard.putNumber("distError", distError);
    }

    public void end(boolean interrupted) {
        if (interrupted) {
            m_drive.arcadeDrive(0, 0);
            System.out.println("Stopped Limelight Aim");
        }
    }

    public boolean isFinished() {
        integralSumX = 0;
        integralSumDist = 0;
        return false;
    }
}
